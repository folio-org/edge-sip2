package org.folio.edge.sip2.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WebClientUtils {

  public static final String SYS_PORT = "port";
  public static final String SYS_NET_SERVER_OPTIONS = "netServerOptions";
  public static final String SYS_PEM_KEY_CERT_OPTIONS = "pemKeyCertOptions";
  public static final String SYS_PFX_KEY_CERT_OPTIONS = "pfxKeyCertOptions";
  public static final String SYS_CERT_PATHS = "certPaths";
  private static final Logger log = LogManager.getLogger();

  private WebClientUtils() {
  }

  /**
   * Create WebClient with TLS.
   * @param vertx instance
   * @param config json config
   * @return WebClient
   */
  public static WebClient create(Vertx vertx, JsonObject config) {
    WebClientOptions options = new WebClientOptions();
    JsonObject netServerOptions = config.getJsonObject(SYS_NET_SERVER_OPTIONS);

    if (netServerOptions != null) {
      JsonObject pfx = netServerOptions.getJsonObject(SYS_PFX_KEY_CERT_OPTIONS);
      JsonObject pem = netServerOptions.getJsonObject(SYS_PEM_KEY_CERT_OPTIONS);
      if (pfx != null) {
        options
          .setSsl(true)
          .setTrustOptions(new PfxOptions()
            .setPath(pfx.getString("path"))
            .setPassword(pfx.getString("password")))
            .setVerifyHost(false);

        log.info("Creating WebClient with TLS on (using PFX truststore)...");
        return WebClient.create(vertx, options);
      }

      if (pem != null) {
        JsonArray certPaths = pem.getJsonArray("certPaths");
        if (certPaths == null || certPaths.isEmpty()) {
          throw new WebClientConfigException("No TLS certPaths were found (pemKeyCertOptions)");
        }
        PemTrustOptions trust = new PemTrustOptions();
        certPaths.forEach(path -> trust.addCertPath((String) path));
        options
          .setSsl(true)
          .setTrustOptions(trust)
            .setVerifyHost(false);

        log.info("Creating WebClient with TLS on (using PEM truststore)...");
        return WebClient.create(vertx, options);
      }
    }

    log.info("Creating WebClient with TLS off...");
    return WebClient.create(vertx, options);
  }
}
