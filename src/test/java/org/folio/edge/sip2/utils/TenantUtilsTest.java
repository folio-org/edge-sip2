package org.folio.edge.sip2.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TenantUtilsTest {

  private static final String MULTI_TENANT_CONFIG = """
      {
        "port": 6443,
        "okapiUrl": "http://okapi:9130",
        "scTenants": [
          {
            "scSubnet": "11.00.00.00/8",
            "tenant": "testTenant11",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          },
          {
            "scSubnet": "22.22.00.00/16",
            "tenant": "testTenant22",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          },
          {
            "scSubnet": "33.33.33.00/24",
            "tenant": "testTenant33",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          },
          {
            "scSubnet": "44.44.44.44/32",
            "tenant": "testTenant44",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          }
        ],
        "tenant": "defaultTenant",
        "errorDetectionEnabled": true,
        "messageDelimiter": "\\r",
        "charset": "ISO-8859-1"
      }""";

  private static final String MULTI_TENANT_CONFIG_WITH_PORT = """
      {
        "ports": [6443, 6444, 6445],
        "okapiUrl": "http://okapi:9130",
        "scTenants": [
          {
            "scSubnet": "11.11.00.00/8",
            "port": "6443",
            "tenant": "testTenant11",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          },
          {
            "scSubnet": "22.22.00.00/16",
            "port": "6444",
            "tenant": "testTenant22",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          },
          {
            "scSubnet": "33.33.00.00/24",
            "port": "6445",
            "tenant": "testTenant33",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          },
          {
            "scSubnet": "44.44.44.44/32",
            "tenant": "testTenant44",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          }
        ],
        "tenant": "defaultTenant",
        "errorDetectionEnabled": true,
        "messageDelimiter": "\\r",
        "charset": "ISO-8859-1"
      }""";

  private static final String MULTI_TENANT_CONFIG_IPV6 = """
      {
        "port": 6443,
        "okapiUrl": "http://okapi:9130",
        "scTenants": [
          {
            "scSubnet": "1001:db8:1::/48",
            "tenant": "tenant1",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          },
          {
            "scSubnet": "2001:db8:2::/64",
            "tenant": "tenant2",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          },
          {
            "scSubnet": "3001:db8:3:4::/80",
            "tenant": "tenant3",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          },
          {
            "scSubnet": "4001:db8:5:6:7:8:9:a/128",
            "tenant": "tenant4",
            "errorDetectionEnabled": true,
            "messageDelimiter": "\\r",
            "charset": "ISO-8859-1"
          }
        ],
        "tenant": "defaultTenant",
        "errorDetectionEnabled": true,
        "messageDelimiter": "\\r",
        "charset": "ISO-8859-1"
      }""";

  private static final String NON_MULTI_TENANT_CONFIG = """
      {
        "ports": [6443],
        "okapiUrl": "http://okapi:9130",
        "tenant": "default",
        "errorDetectionEnabled": true,
        "messageDelimiter": "\\r",
        "charset": "ISO-8859-1"
      }""";

  private static final int DEFAULT_PORT = 0;

  @Test
  void testNonMultiTenantConfig() {
    assertEquals("default", getTenantFromPort(NON_MULTI_TENANT_CONFIG, "1.2.3.4", DEFAULT_PORT));
  }

  @ParameterizedTest(name = "[{index}][{0}] {1}:{2} -> {3}")
  @CsvSource(nullValues = "null", value = {
      "invalid IP value, test, 0, defaultTenant",
      "Lo11, 11.0.0.0, 0, testTenant11",
      "Mid11, 11.128.128.128, 0, testTenant11",
      "Hi11, 11.255.255.255, 0, testTenant11",

      "Lo22, 22.22.0.0, 0, testTenant22",
      "Mid22, 22.22.128.128, 0, testTenant22",
      "Hi22, 22.22.255.255, 0, testTenant22",
      "Lo22 (unmatched), 22.0.0.0, 0, defaultTenant",
      "Hi22 (unmatched), 22.255.255.255, 0, defaultTenant",

      "Lo33, 33.33.33.0, 0, testTenant33",
      "Mid33, 33.33.33.128, 0, testTenant33",
      "Hi33, 33.33.33.255, 0, testTenant33",
      "Lo33 (unmatched 1), 33.0.0.0, 0, defaultTenant",
      "Lo33 (unmatched 2), 33.33.0.0, 0, defaultTenant",
      "Hi33 (unmatched 1), 33.33.255.255, 0, defaultTenant",
      "Hi33 (unmatched 2), 33.255.255.255, 0, defaultTenant",

      "exact 44, 44.44.44.44, 0, testTenant44",
      "exact 44 (ip and port), 44.44.44.44, 6443, testTenant44",
      "bad ip, 44.44.128.128, 6443, defaultTenant",
  })
  @DisplayName("testMultiTenantConfig_parameterized")
  void testMultiTenantConfig_parameterized(@SuppressWarnings("unused") String name,
      String ip, int port, String expectedTenant) {

    assertEquals(expectedTenant, getTenantFromPort(MULTI_TENANT_CONFIG, ip, port));
  }

  @ParameterizedTest(name = "[{index}][{0}] {1}:{2} -> {3}")
  @CsvSource(nullValues = "null", value = {
      // Tenant 1: /48 subnet cases
      "Lower range tenant1, 1001:db8:1::1, 0, tenant1",
      "Mid tenant1, 1001:db8:1:8000::1, 0, tenant1",
      "Upper range tenant1, 1001:db8:1:ffff:ffff:ffff:ffff:ffff, 0, tenant1",
      "Non-boundary tenant1, 1001:db8:2::1, 0, defaultTenant",

      // Tenant 2: /64 subnet cases
      "Exact match tenant2, 2001:db8:2::, 0, tenant2",
      "Upper boundary tenant2, 2001:db8:2:0:0:0:0:1, 0, tenant2",
      "Outside range tenant2 (lower), 2001:db8:1:ffff::1, 0, defaultTenant",
      "Outside range tenant2 (upper), 2001:db8:3::1, 0, defaultTenant",

      // Tenant 3: /80 subnet cases
      "Mid-range, 3001:db8:3:4::8000, 0, tenant3",
      "Exact match tenant3, 3001:db8:3:4::, 0, tenant3",
      "Inside tenant3, 3001:db8:3:4:0:0:0:1, 0, tenant3",
      "Boundary plus one tenant3, 3001:db8:3:4:1::, 0, defaultTenant",

      // Tenant 4: /128 exact match cases
      "Exact match tenant4, 4001:db8:5:6:7:8:9:a, 0, tenant4",
      "Similar IP but different tenant4, 4001:db8:5:6:7:8:9:b, 0, defaultTenant",

      // Edge cases - IPv6
      "Upper full range, ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff, 0, defaultTenant",
      "Zero network, ::1, 0, defaultTenant",
      "Non-matching IP, 2001:foo:bar::, 0, defaultTenant"
  })
  @DisplayName("testMultiTenantConfigIPv6_parameterized")
  void testMultiTenantConfigIPv6_parameterized(@SuppressWarnings("unused") String name,
      String ip, int port, String expectedTenant) {

    assertEquals(expectedTenant, getTenantFromPort(MULTI_TENANT_CONFIG_IPV6, ip, port));
  }

  @ParameterizedTest(name = "[{index}][{0}] {1}:{2} -> {3}")
  @CsvSource(nullValues = "null", value = {
      "unknown ip and default port, 1.2.3.4, 0, defaultTenant",
      "unknown ip and port, 1.2.3.4, 1000, defaultTenant",
      "exact 44 (port2), 44.44.44.44, 6443, testTenant11",
      "exact 44 (port3), 44.44.44.44, 6444, testTenant22",
      "exact 44 (port4), 44.44.44.44, 6445, testTenant33",
      "exact 44 (range match), 44.44.44.44, 6447, testTenant44",
  })
  @DisplayName("testMultiTenantConfig_parameterized")
  void testMultiTenantPortConfig_parameterized(@SuppressWarnings("unused") String name,
      String ip, int port, String expectedTenant) {

    assertEquals(expectedTenant, getTenantFromPort(MULTI_TENANT_CONFIG_WITH_PORT, ip, port));
  }

  @Test
  void testNonValidTenantConfig() {
    var config = """
        {
          "ports": [6443],
          "okapiUrl": "http://okapi:9130",
          "tenant": "defaultTenant",
          "errorDetectionEnabled": true,
          "messageDelimiter": "\\r",
          "charset": "ISO-8859-1",
          "scTenants": [
            {
              "scSubnet": "invalid subnet",
              "tenant": "testTenant11",
              "errorDetectionEnabled": true,
              "messageDelimiter": "\\r",
              "charset": "ISO-8859-1"
            }
          ]
        }""";
    assertEquals("defaultTenant", getTenantFromPort(config, "1.2.3.4", DEFAULT_PORT));
  }

  @Test
  void testNonValidTenantConfigIPv6() {
    String config = """
        {
          "ports": [6443],
          "okapiUrl": "http://okapi:9130",
          "tenant": "defaultTenant",
          "errorDetectionEnabled": true,
          "messageDelimiter": "\\r",
          "charset": "ISO-8859-1",
          "scTenants": [
            {
              "scSubnet": "invalid subnet",
              "tenant": "tenant1",
              "errorDetectionEnabled": true,
              "messageDelimiter": "\\r",
              "charset": "ISO-8859-1"
            }
          ]
        }""";
    assertEquals("defaultTenant", getTenantFromPort(config, "2001:db8:ffff::1", DEFAULT_PORT));
  }

  private String getTenantFromPort(String config, String ip, int port) {
    JsonObject tc = TenantUtils.lookupTenantConfigForIpAddress(new JsonObject(config), ip, port);
    return tc.getString("tenant");
  }
}
