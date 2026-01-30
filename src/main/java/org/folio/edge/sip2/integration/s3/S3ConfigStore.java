package org.folio.edge.sip2.integration.s3;

import io.vertx.config.spi.ConfigStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.s3.client.FolioS3Client;

public class S3ConfigStore implements ConfigStore {

  private final Sip2LogAdapter log = Sip2LogAdapter.getLogger(S3ConfigStore.class);
  private final String key;
  private final Vertx vertx;
  private final FolioS3Client s3;

  /**
   * Constructs a new {@link S3ConfigStore} instance.
   *
   * @param vertx    - the Vertx instance to use for asynchronous operations
   * @param s3Client - the FolioS3Client to interact with S3
   * @param key      - the S3 object key where the configuration is stored
   * @throws IllegalStateException - if the S3 client cannot be created due to invalid configuration
   */
  public S3ConfigStore(Vertx vertx, FolioS3Client s3Client, String key) {
    this.key = key;
    this.vertx = vertx;
    this.s3 = s3Client;
  }

  @Override
  public Future<Buffer> get() {
    return vertx.executeBlocking(() -> {
      try (var getRequest = s3.read(key)) {
        var resultBuffer = Buffer.buffer(getRequest.readAllBytes());
        log.debug("S3 configuration loaded successfully from key: {}", key);
        return resultBuffer;
      } catch (Exception e) {
        log.error("Failed to load tenant configuration from S3 for key: {}", key, e);
        throw new IllegalStateException("Failed to load tenant configuration from S3", e);
      }
    });
  }

  @Override
  public Future<Void> close() {
    // S3 client is managed externally, no cleanup needed here
    return Future.succeededFuture();
  }
}
