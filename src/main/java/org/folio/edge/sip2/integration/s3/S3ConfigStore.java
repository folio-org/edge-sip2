package org.folio.edge.sip2.integration.s3;

import io.vertx.config.spi.ConfigStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.folio.s3.client.FolioS3Client;
import org.folio.s3.client.S3ClientFactory;
import org.folio.s3.client.S3ClientProperties;

public class S3ConfigStore implements ConfigStore {

  private final String key;
  private final Vertx vertx;
  private final FolioS3Client s3;

  /**
   * Constructs a new {@link S3ConfigStore} instance.
   *
   * @param vertx  - the Vertx instance to use for asynchronous operations
   * @param config - the configuration object containing S3 connection properties
   * @throws Error - if the S3 client cannot be created due to invalid configuration
   */
  public S3ConfigStore(Vertx vertx, JsonObject config) {
    try {
      this.vertx = vertx;
      this.key = config.getString("key");

      s3 = createFolioS3Client(config);
    } catch (Exception e) {
      throw new Error(e);
    }
  }

  @Override
  public Future<Buffer> get() {
    return vertx.executeBlocking(() -> {
      try (var getRequest = s3.read(key)) {
        return Buffer.buffer(getRequest.readAllBytes());
      } catch (Exception e) {
        throw new IllegalStateException("Failed to load tenant configuration from S3", e);
      }
    });
  }

  private FolioS3Client createFolioS3Client(JsonObject conf) {
    var configuration = S3ClientProperties.builder()
        .endpoint(conf.getString("endpoint_url"))
        .accessKey(getRequiredValue(conf, "access_key"))
        .secretKey(getRequiredValue(conf, "secret_access_key"))
        .bucket(getRequiredValue(conf, "bucket"))
        .region(getRequiredValue(conf, "region"))
        .build();
    return S3ClientFactory.getS3Client(
        configuration
    );
  }

  private static String getRequiredValue(JsonObject conf, String key) {
    var configValue = conf.getString(key);
    if (configValue == null) {
      throw new IllegalArgumentException("'" + key + "' is required for s3 configuration");
    }
    return configValue;
  }
}
