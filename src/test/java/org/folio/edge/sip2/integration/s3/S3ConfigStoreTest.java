package org.folio.edge.sip2.integration.s3;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.folio.s3.client.FolioS3Client;
import org.folio.s3.exception.S3ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
class S3ConfigStoreTest {

  private static final String TENANT_CONF_FILE_KEY = "sip2/sip2-tenant.json";
  private S3ConfigStore s3ConfigStore;
  @Mock private FolioS3Client folioS3Client;

  @BeforeEach
  void setUp(Vertx vertx) {
    s3ConfigStore = new S3ConfigStore(vertx, folioS3Client, TENANT_CONF_FILE_KEY);
  }

  @Test
  void get_positive(VertxTestContext testContext) {
    var fileContent = new ByteArrayInputStream(scTenantsContent().getBytes(UTF_8));

    when(folioS3Client.read(TENANT_CONF_FILE_KEY)).thenReturn(fileContent);
    var resultFuture = s3ConfigStore.get();
    resultFuture.onComplete(testContext.succeeding(ar -> {
      var parsedContent = new JsonObject(ar);
      assertEquals(scTenantsContent(), parsedContent.encode());
      testContext.completeNow();
    }));
  }

  @Test
  void get_negative_s3ClientException(VertxTestContext testContext) {
    var exception = new S3ClientException("Simulated S3 failure");
    when(folioS3Client.read(TENANT_CONF_FILE_KEY)).thenThrow(exception);

    var resultFuture = s3ConfigStore.get();
    resultFuture.onComplete(testContext.failing(error -> {
      assertInstanceOf(IllegalStateException.class, error);
      assertEquals("Failed to load tenant configuration from S3", error.getMessage());
      assertInstanceOf(S3ClientException.class, error.getCause());
      testContext.completeNow();
    }));
  }

  private static String scTenantsContent() {
    var scTenantObject = new JsonObject()
        .put("port", 6443)
        .put("scSubnet", "0.0.0.0/0")
        .put("tenant", "testtenant")
        .put("errorDetectionEnabled", true)
        .put("messageDelimiter", "\r")
        .put("fieldDelimiter", "|")
        .put("charset", "ISO-8859-1");

    return new JsonObject()
        .put("scTenants", new JsonArray(List.of(scTenantObject)))
        .encode();
  }
}
