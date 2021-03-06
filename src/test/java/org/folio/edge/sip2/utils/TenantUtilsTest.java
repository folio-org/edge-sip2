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
  
  private static String nonMultiTenantConfig = ""
      + "{" 
      + "  \"port\": 6443," 
      + "  \"okapiUrl\": \"http://${okapi_lb_url}:${okapi_port}\"," 
      + "  \"tenant\": \"default\"," 
      + "  \"errorDetectionEnabled\": true," 
      + "  \"messageDelimiter\": \"\\r\"," 
      + "  \"charset\": \"ISO-8859-1\"" 
      + "}";
  
  @Test
  void testNonMultiTenantConfig() {

    assertEquals("default", getTenant(nonMultiTenantConfig, "1.2.3.4"));
  }
  
  @Test
  void testMultiTenantConfig_Lo11() {

    assertEquals("testTenant11", getTenant(multiTenantConfig, "11.11.0.0"));
  }
  
  @Test
  void testMultiTenantConfig_Mid11() {

    assertEquals("testTenant11", getTenant(multiTenantConfig, "11.11.128.128"));
  }
  
  @Test
  void testMultiTenantConfig_Hi11() {

    assertEquals("testTenant11", getTenant(multiTenantConfig, "11.11.255.255"));
  }

  @Test
  void testMultiTenantConfig_Mid22() {

    assertEquals("testTenant22", getTenant(multiTenantConfig, "22.22.128.128"));
  }
  
  @Test
  void testMultiTenantConfig_Mid33() {

    assertEquals("testTenant33", getTenant(multiTenantConfig, "33.33.128.128"));
  }
  
  @Test
  void testMultiTenantConfig_exact44() {

    assertEquals("testTenant44", getTenant(multiTenantConfig, "44.44.44.44"));
  }
  
  @Test
  void testBadIpMultiTenantConfig() {

    assertEquals("default", getTenant(multiTenantConfig, "44.44.128.128"));
  }
  
  
  private String getTenant(String config, String ip) {
    JsonObject tc = TenantUtils.lookupTenantConfigForIPaddress(new JsonObject(config), ip);
    return tc.getString("tenant");
  }


}
