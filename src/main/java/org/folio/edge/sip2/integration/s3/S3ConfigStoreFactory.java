package org.folio.edge.sip2.integration.s3;

import io.vertx.config.spi.ConfigStore;
import io.vertx.config.spi.ConfigStoreFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.s3.client.FolioS3Client;
import org.folio.s3.client.S3ClientFactory;
import org.folio.s3.client.S3ClientProperties;

public class S3ConfigStoreFactory implements ConfigStoreFactory {

  private final Sip2LogAdapter log = Sip2LogAdapter.getLogger(S3ConfigStoreFactory.class);
  private static final String NAME = "s3";

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public ConfigStore create(Vertx vertx, JsonObject configuration) {
    try {
      var s3ObjectKey = getRequiredValue(configuration, "key");
      var s3ConfigStore = new S3ConfigStore(vertx, createFolioS3Client(configuration), s3ObjectKey);
      log.debug("S3ConfigStore created for s3 object key: {}", s3ObjectKey);
      return s3ConfigStore;
    } catch (Exception e) {
      log.error("Failed to create S3ConfigStore", e);
      throw new IllegalStateException("Failed to create S3ConfigStore", e);
    }
  }

  private FolioS3Client createFolioS3Client(JsonObject conf) {
    var region = getRequiredValue(conf, "region");
    var endpointUrl = getEndpointUrl(conf, region);

    var configuration = S3ClientProperties.builder()
        .endpoint(endpointUrl)
        .accessKey(conf.getString("access_key"))
        .secretKey(conf.getString("secret_access_key"))
        .bucket(getRequiredValue(conf, "bucket"))
        .region(region)
        .awsSdk(true)
        .build();

    return S3ClientFactory.getS3Client(configuration);
  }

  private static String getEndpointUrl(JsonObject conf, String region) {
    var configEndpointUrl = conf.getString("endpoint_url");
    return StringUtils.isNotBlank(configEndpointUrl)
        ? configEndpointUrl.trim()
        : String.format("https://s3.%s.amazonaws.com", region);
  }

  private static String getRequiredValue(JsonObject conf, String key) {
    var configValue = conf.getString(key);
    if (configValue == null) {
      throw new IllegalArgumentException("'" + key + "' is required for s3 configuration");
    }

    return configValue;
  }
}
