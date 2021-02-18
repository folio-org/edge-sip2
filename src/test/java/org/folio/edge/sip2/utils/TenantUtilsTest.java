package org.folio.edge.sip2.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

public class TenantUtilsTest {

  private static String multiTenantConfig = ""
      + "{" 
      + "  \"port\": 6443," 
      + "  \"okapiUrl\": \"http://${okapi_lb_url}:${okapi_port}\"," 
      + "  \"scTenants\": [" 
      + "    {" 
      + "      \"scSubnet\": \"11.11.00.00/16\"," 
      + "      \"tenant\": \"fs00000011\"," 
      + "      \"errorDetectionEnabled\": true," 
      + "      \"messageDelimiter\": \"\\r\"," 
      + "      \"charset\": \"ISO-8859-1\"" 
      + "    }," 
      + "    {" 
      + "      \"scSubnet\": \"22.22.00.00/16\"," 
      + "      \"tenant\": \"fs00000022\"," 
      + "      \"errorDetectionEnabled\": true," 
      + "      \"messageDelimiter\": \"\\r\"," 
      + "      \"charset\": \"ISO-8859-1\"" 
      + "    }," 
      + "    {" 
      + "      \"scSubnet\": \"33.33.00.00/16\"," 
      + "      \"tenant\": \"fs00000033\"," 
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
  public void testNonMultiTenantConfig() {

    assertEquals(getTenant(nonMultiTenantConfig, "1.2.3.4"), "default");
  }
  
  @Test
  public void testMultiTenantConfig_Lo11() {

    assertEquals(getTenant(multiTenantConfig, "11.11.0.0"), "fs00000011");
  }
  
  @Test
  public void testMultiTenantConfig_Mid11() {

    assertEquals(getTenant(multiTenantConfig, "11.11.128.128"), "fs00000011");
  }
  
  @Test
  public void testMultiTenantConfig_Hi11() {

    assertEquals(getTenant(multiTenantConfig, "11.11.255.255"), "fs00000011");
  }

  @Test
  public void testMultiTenantConfig_Mid22() {

    assertEquals(getTenant(multiTenantConfig, "22.22.128.128"), "fs00000022");
  }
  
  @Test
  public void testMultiTenantConfig_Mid33() {

    assertEquals(getTenant(multiTenantConfig, "33.33.128.128"), "fs00000033");
  }
  
  @Test
  public void testBadIpMultiTenantConfig() {

    assertEquals(getTenant(multiTenantConfig, "44.44.128.128"), "default");
  }
  
  
  private String getTenant(String config, String ip) {
    JsonObject tc = TenantUtils.lookupTenantConfigForIPaddress(new JsonObject(config), ip);
    return tc.getString("tenant");
  }


}
