package org.folio.edge.sip2.integration.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.Objects;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class S3ConfigStoreFactoryTest {

  @InjectMocks private S3ConfigStoreFactory s3ConfigStoreFactory;

  @ParameterizedTest
  @ValueSource(strings = {
      "",
      "null",
      "http://localhost:4566",
      "https://s3.us-west-2.amazonaws.com"
  })
  void create_positive(String endpointUrl) {
    var configuration = s3ConfigStoreConfiguration(endpointUrl);
    var result = s3ConfigStoreFactory.create(Vertx.vertx(), configuration);
    assertNotNull(result);
  }

  @Test
  void create_negative() {
    var configuration = s3ConfigStoreConfiguration(null);
    configuration.put("bucket", null);
    var exception = assertThrows(IllegalStateException.class,
        () -> s3ConfigStoreFactory.create(Vertx.vertx(), configuration));
    assertEquals("Failed to create S3ConfigStore", exception.getMessage());
    assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    assertEquals("'bucket' is required for s3 configuration", exception.getCause().getMessage());
  }

  private static JsonObject s3ConfigStoreConfiguration(String endpointUrl) {
    return new JsonObject()
        .put("key", "sip2/sip2-tenant.json")
        .put("endpoint_url", Objects.equals(endpointUrl, "null") ? null : endpointUrl)
        .put("access_key", "accessKey")
        .put("secret_access_key", "secretAccessKey")
        .put("bucket", "test-bucket")
        .put("region", "us-east-1");
  }
}
