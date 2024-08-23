package org.folio.edge.sip2.utils;

import io.vertx.core.json.JsonObject;
import java.util.Optional;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TenantUtils {
  private static final Logger log = LogManager.getLogger();
  private static final String SC_TENANTS = "scTenants";
  private static final String SC_SUBNET = "scSubnet";

  private TenantUtils() {
    super();
  }

  /**
   * Returns JSON config for tenant whose subnet encompasses a client IP address.
   *
   * @param sip2config - SIP2 edge module config.
   *     Contains a JSON array (scTenants) of tenant configs,
   *     each with a scSubnet element with a CIDR-notation string value.
   *     <p>
   *     I.E:<br>
   *     <pre>
   *     {
   *      "scTenants": [
   *        {
   *          "scSubnet": "11.11.00.00/16",
   *          "tenant": "diku",
   *          "errorDetectionEnabled": true,
   *          "messageDelimiter": "\r",
   *          "charset": "ISO-8859-1"
   *        },
   *        {
   *          "scSubnet": "22.22.00.00/16",
   *          "tenant": "test_tenant",
   *          "errorDetectionEnabled": true,
   *          "messageDelimiter": "\r",
   *          "charset": "ISO-8859-1"
   *        },
   *        {
   *          "scSubnet": "33.33.00.00/16",
   *          "tenant": "test_tenant",
   *          "errorDetectionEnabled": true,
   *          "messageDelimiter": "\r",
   *          "charset": "ISO-8859-1"
   *        }
   *      ]
   *     }
   *     </pre>
   *     </p>
   * @param clientIP - IPv4 address of client SC used as lookup to find tenant config
   * @param port Tenant specific port value.
   * @return tenant config whose scSubnet encompasses clientIP.
   *     Returns the sip2conf itself if it does not contain a scTenants element or
   *     the scTenants array has no tenant with subnet in range for clientIP.
   */
  public static JsonObject lookupTenantConfigForIpAddress(JsonObject sip2config, String clientIP,
                                                          int port) {

    if (!sip2config.containsKey(SC_TENANTS)) {
      log.debug("LookupTenantConfig scTenants key not found in config, "
          + "support for muti-tenant not available");
      return sip2config;
    }

    Optional<JsonObject> tenantConfigOptional = sip2config.getJsonArray(SC_TENANTS).stream()
        .map(o -> (JsonObject) o)
        .filter(jo -> jo.containsKey("port") && Integer.parseInt(jo.getString("port")) == port)
        .findFirst();

    if (tenantConfigOptional.isPresent()) {
      return tenantConfigOptional.get();
    }

    // If no port match, check if the client IP is in range
    tenantConfigOptional = sip2config.getJsonArray(SC_TENANTS).stream()
      .map(o -> (JsonObject) o)
      .filter(jo -> {
        SubnetUtils sn = new SubnetUtils(jo.getString(SC_SUBNET));
        sn.setInclusiveHostCount(true);
        return sn.getInfo().isInRange(clientIP);
      })
      .findFirst();

    return tenantConfigOptional.orElse(sip2config);
  }
}
