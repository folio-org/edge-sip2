package org.folio.edge.sip2.service.tenant;

import static org.folio.edge.sip2.domain.TenantResolutionContext.createContextForLoginPhase;
import static org.folio.edge.sip2.domain.type.TenantResolutionPhase.LOGIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.UUID;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class LocationCodeTenantResolverTest {

  @InjectMocks private LocationCodeTenantResolver tenantResolver;

  @Test
  void resolve_positive() {
    var locationCode = generateLocationCode();
    var locationCodes = List.of(locationCode);
    var multiTenantConfig = multiTenantConfig(locationCodes);
    var sip2Configuration = new JsonObject()
        .put("scTenants", new JsonArray(List.of(multiTenantConfig)));

    var sessionData = testSessionData(locationCode);
    var tenantContext = createContextForLoginPhase(sip2Configuration, sessionData);

    var result = tenantResolver.resolve(tenantContext);

    assertTrue(result.isPresent());
    assertEquals(multiTenantConfig, result.get());
  }

  @Test
  void resolve_positive_locationNotMatched() {
    var locationCodes = List.of(generateLocationCode());
    var multiTenantConfig = multiTenantConfig(locationCodes);
    var sip2Configuration = new JsonObject()
        .put("scTenants", new JsonArray(List.of(multiTenantConfig)));

    var sessionData = testSessionData(generateLocationCode());
    var tenantContext = createContextForLoginPhase(sip2Configuration, sessionData);

    var result = tenantResolver.resolve(tenantContext);

    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_positive_locationCodeIsNotSet() {
    var locationCodes = List.of(generateLocationCode());
    var multiTenantConfig = multiTenantConfig(locationCodes);
    var sip2Configuration = new JsonObject()
        .put("scTenants", new JsonArray(List.of(multiTenantConfig)));

    var sessionData = testSessionData(null);
    var tenantContext = createContextForLoginPhase(sip2Configuration, sessionData);

    var result = tenantResolver.resolve(tenantContext);

    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_positive_scTenantsIsEmpty() {
    var locationCode = generateLocationCode();
    var sip2Configuration = new JsonObject().put("scTenants", new JsonArray());

    var sessionData = testSessionData(locationCode);
    var tenantContext = createContextForLoginPhase(sip2Configuration, sessionData);

    var result = tenantResolver.resolve(tenantContext);

    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_positive_locationCodesContainNonStringValues() {
    var locationCode = generateLocationCode();
    var multiTenantConfig = new JsonObject().put("locationCodes", new JsonArray(List.of(1,2,3)));
    var sip2Configuration = new JsonObject()
        .put("scTenants", new JsonArray(List.of(multiTenantConfig)));

    var sessionData = testSessionData(locationCode);
    var tenantContext = createContextForLoginPhase(sip2Configuration, sessionData);

    var result = tenantResolver.resolve(tenantContext);

    assertTrue(result.isEmpty());
  }

  @Test
  void getPhase_positive() {
    var result = tenantResolver.getPhase();
    assertEquals(LOGIN, result);
  }

  @Test
  void getName_positive() {
    var result = tenantResolver.getName();
    assertEquals("LOCATION_CODE", result);
  }

  private static String generateLocationCode() {
    return UUID.randomUUID().toString();
  }

  private static JsonObject multiTenantConfig(Object locationCodes) {
    return new JsonObject()
        .put("locationCodes", locationCodes)
        .put("tenant", "test");
  }

  private static SessionData testSessionData(String locationCode) {
    var sessionData = SessionData.createSession("test", '|', false, "UTF-8");
    sessionData.setScLocation(locationCode);
    return sessionData;
  }
}
