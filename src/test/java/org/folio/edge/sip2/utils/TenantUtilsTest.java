package org.folio.edge.sip2.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

class TenantUtilsTest {

  private static String multiTenantConfig = ""
      + "{"
      + "  \"port\": 6443,"
      + "  \"okapiUrl\": \"http://${okapi_lb_url}:${okapi_port}\","
      + "  \"scTenants\": ["
      + "    {"
      + "      \"scSubnet\": \"11.11.00.00/16\","
      + "      \"tenant\": \"testTenant11\","
      + "      \"errorDetectionEnabled\": true,"
      + "      \"messageDelimiter\": \"\\r\","
      + "      \"charset\": \"ISO-8859-1\""
      + "    },"
      + "    {"
      + "      \"scSubnet\": \"22.22.00.00/16\","
      + "      \"tenant\": \"testTenant22\","
      + "      \"errorDetectionEnabled\": true,"
      + "      \"messageDelimiter\": \"\\r\","
      + "      \"charset\": \"ISO-8859-1\""
      + "    },"
      + "    {"
      + "      \"scSubnet\": \"33.33.00.00/16\","
      + "      \"tenant\": \"testTenant33\","
      + "      \"errorDetectionEnabled\": true,"
      + "      \"messageDelimiter\": \"\\r\","
      + "      \"charset\": \"ISO-8859-1\""
      + "    },"
      + "    {"
      + "      \"scSubnet\": \"44.44.44.44/32\","
      + "      \"tenant\": \"testTenant44\","
      + "      \"errorDetectionEnabled\": true,"
      + "      \"messageDelimiter\": \"\\r\","
      + "      \"charset\": \"ISO-8859-1\""
      + "    }"
      + "  ],"
      + "  \"tenant\": \"default\","
      + "  \"errorDetectionEnabled\": true,"
      + "  \"messageDelimiter\": \"\\r\","
      + "  \"charset\": \"ISO-8859-1\""
      + "}";

  private static String multiTenantConfigWithPort = ""
      + "{"
      + "  \"ports\": [6443, 6444, 6445],"
      + "  \"okapiUrl\": \"http://${okapi_lb_url}:${okapi_port}\","
      + "  \"scTenants\": ["
      + "    {"
      + "      \"scSubnet\": \"11.11.00.00/16\","
      + "      \"port\": \"6443\","
      + "      \"tenant\": \"testTenant11\","
      + "      \"errorDetectionEnabled\": true,"
      + "      \"messageDelimiter\": \"\\r\","
      + "      \"charset\": \"ISO-8859-1\""
      + "    },"
      + "    {"
      + "      \"scSubnet\": \"22.22.00.00/16\","
      + "      \"port\": \"6444\","
      + "      \"tenant\": \"testTenant22\","
      + "      \"errorDetectionEnabled\": true,"
      + "      \"messageDelimiter\": \"\\r\","
      + "      \"charset\": \"ISO-8859-1\""
      + "    },"
      + "    {"
      + "      \"scSubnet\": \"33.33.00.00/16\","
      + "      \"port\": \"6445\","
      + "      \"tenant\": \"testTenant33\","
      + "      \"errorDetectionEnabled\": true,"
      + "      \"messageDelimiter\": \"\\r\","
      + "      \"charset\": \"ISO-8859-1\""
      + "    },"
      + "    {"
      + "      \"scSubnet\": \"44.44.44.44/32\","
      + "      \"tenant\": \"testTenant44\","
      + "      \"errorDetectionEnabled\": true,"
      + "      \"messageDelimiter\": \"\\r\","
      + "      \"charset\": \"ISO-8859-1\""
      + "    }"
      + "  ],"
      + "  \"tenant\": \"defaultTenant\","
      + "  \"errorDetectionEnabled\": true,"
      + "  \"messageDelimiter\": \"\\r\","
      + "  \"charset\": \"ISO-8859-1\""
      + "}";



  private static String nonMultiTenantConfig = ""
      + "{"
      + "  \"ports\": [6443],"
      + "  \"okapiUrl\": \"http://${okapi_lb_url}:${okapi_port}\","
      + "  \"tenant\": \"default\","
      + "  \"errorDetectionEnabled\": true,"
      + "  \"messageDelimiter\": \"\\r\","
      + "  \"charset\": \"ISO-8859-1\""
      + "}";



  private static int defaultPort = 0;

  @Test
  void testNonMultiTenantConfig() {

    assertEquals("default", getTenantFromPort(nonMultiTenantConfig, "1.2.3.4", defaultPort));
  }

  @Test
  void testNonMultiTenantPortConfig() {

    assertEquals("defaultTenant", getTenantFromPort(multiTenantConfigWithPort, "1.2.3.4",
        defaultPort));
  }

  @Test
  void testNonMultiTenantPortConfigIncorrectPort() {

    assertEquals("defaultTenant", getTenantFromPort(multiTenantConfigWithPort, "1.2.3.4",1000));
  }

  @Test
  void testMultiTenantConfig_Lo11() {

    assertEquals("testTenant11", getTenantFromPort(multiTenantConfig, "11.11.0.0", defaultPort));
  }

  @Test
  void testMultiTenantConfig_Mid11() {

    assertEquals("testTenant11", getTenantFromPort(multiTenantConfig, "11.11.128.128",
        defaultPort));
  }

  @Test
  void testMultiTenantConfig_Hi11() {

    assertEquals("testTenant11", getTenantFromPort(multiTenantConfig, "11.11.255.255",
        defaultPort));
  }

  @Test
  void testMultiTenantConfig_Mid22() {

    assertEquals("testTenant22", getTenantFromPort(multiTenantConfig, "22.22.128.128",
        defaultPort));
  }

  @Test
  void testMultiTenantConfig_Mid33() {

    assertEquals("testTenant33", getTenantFromPort(multiTenantConfig, "33.33.128.128",
        defaultPort));
  }

  @Test
  void testMultiTenantConfig_exact44() {

    assertEquals("testTenant44", getTenantFromPort(multiTenantConfig, "44.44.44.44", defaultPort));
  }

  @Test
  void testMultiTenantConfig_exact44_withRangeAndNoPort() {

    assertEquals("testTenant44", getTenantFromPort(multiTenantConfig, "44.44.44.44", 6443));
  }

  @Test
  void testMultiTenantConfig_exact44_withPort2() {

    assertEquals("testTenant11", getTenantFromPort(multiTenantConfigWithPort, "44.44.44.44", 6443));
  }

  @Test
  void testMultiTenantConfig_exact44_withPort3() {

    assertEquals("testTenant22", getTenantFromPort(multiTenantConfigWithPort, "44.44.44.44", 6444));
  }

  @Test
  void testMultiTenantConfig_exact44_withPort4() {

    assertEquals("testTenant33", getTenantFromPort(multiTenantConfigWithPort, "44.44.44.44", 6445));
  }


  @Test
  void testMultiTenantConfig_exact44_withRange() {

    assertEquals("testTenant44", getTenantFromPort(multiTenantConfigWithPort, "44.44.44.44", 6447));
  }

  @Test
  void testBadIpMultiTenantConfig() {

    assertEquals("default", getTenantFromPort(multiTenantConfig, "44.44.128.128", defaultPort));
  }


  private String getTenantFromPort(String config, String ip, int port) {
    JsonObject tc = TenantUtils.lookupTenantConfigForIPaddress(new JsonObject(config), ip, port);
    return tc.getString("tenant");
  }


}
