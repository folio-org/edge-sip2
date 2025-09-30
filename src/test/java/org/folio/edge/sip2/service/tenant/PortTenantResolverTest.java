package org.folio.edge.sip2.service.tenant;

import static org.folio.edge.sip2.domain.TenantResolutionContext.createContextForConnectPhase;
import static org.folio.edge.sip2.domain.type.TenantResolutionPhase.CONNECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.vertx.core.json.JsonObject;
import java.util.Optional;
import org.folio.edge.sip2.domain.ConnectionDetails;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class PortTenantResolverTest {

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

  @InjectMocks private PortTenantResolver resolver;

  @ParameterizedTest(name = "[{index}][{0}] {1}:{2} -> {3}")
  @CsvSource(nullValues = "null", value = {
      "unknown ip and default port, 1.2.3.4, 0, null",
      "unknown ip and port, 1.2.3.4, 1000, null",
      "exact 44 (port2), 44.44.44.44, 6443, testTenant11",
      "exact 44 (port3), 44.44.44.44, 6444, testTenant22",
      "exact 44 (port4), 44.44.44.44, 6445, testTenant33",
  })
  @DisplayName("testMultiTenantConfig_parameterized")
  void testMultiTenantPortConfig_parameterized(@SuppressWarnings("unused") String name,
      String clientAddress, int port, String expectedTenant) {

    var configuration = new JsonObject(MULTI_TENANT_CONFIG_WITH_PORT);
    var connectionDetails = ConnectionDetails.of(port, clientAddress);
    var tenantContext = createContextForConnectPhase(configuration, connectionDetails);

    var result = resolver.resolve(tenantContext);

    var actualTenantOptional = result.map(config -> config.getString("tenant"));
    assertEquals(Optional.ofNullable(expectedTenant), actualTenantOptional);
  }

  @Test
  void resolve_positive_invalidPortValue() {
    var config = """
        {
          "scTenants": [
            {
              "port": "invalidPort",
              "tenant": "tenant1",
              "errorDetectionEnabled": true,
              "messageDelimiter": "\\r",
              "charset": "ISO-8859-1"
            }
          ]
        }""";

    var configuration = new JsonObject(config);
    var connectionDetails = ConnectionDetails.of(6443, "127.0.0.1");
    var tenantContext = createContextForConnectPhase(configuration, connectionDetails);

    var result = resolver.resolve(tenantContext);

    assertFalse(result.isPresent());
  }

  @Test
  void getPhase_positive() {
    var result = resolver.getPhase();
    assertEquals(CONNECT, result);
  }

  @Test
  void getName_positive() {
    var result = resolver.getName();
    assertEquals("PORT", result);
  }
}
