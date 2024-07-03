package org.folio.edge.sip2.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WebClientUtils {

  public static final String SYS_PORT = "port";
  public static final String SYS_NET_SERVER_OPTIONS = "netServerOptions";
  public static final String SYS_PEM_KEY_CERT_OPTIONS = "pemKeyCertOptions";
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
    JsonObject netServerOptions = config.getJsonObject(SYS_NET_SERVER_OPTIONS);
    if (Objects.nonNull(netServerOptions)
        && netServerOptions.containsKey(SYS_PEM_KEY_CERT_OPTIONS)) {
      log.info("Creating WebClient with TLS on...");

      JsonArray certPaths = netServerOptions.getJsonObject(SYS_PEM_KEY_CERT_OPTIONS)
          .getJsonArray(SYS_CERT_PATHS);
      if (Objects.isNull(certPaths) || certPaths.isEmpty()) {
        throw new WebClientConfigException("No TLS certPaths were found in config");
      }

      final PemTrustOptions pemTrustOptions = new PemTrustOptions();
      certPaths.forEach(entry -> pemTrustOptions.addCertPath((String) entry));

      final WebClientOptions webClientOptions = new WebClientOptions()
          .setSsl(true)
          .setTrustOptions(pemTrustOptions)
          .setVerifyHost(false); //Hardcoded now. Later it could be configurable using env vars.
      return WebClient.create(vertx, webClientOptions);
    } else {
      log.info("Creating WebClient with TLS off...");
      return WebClient.create(vertx);
    }
  }
}
