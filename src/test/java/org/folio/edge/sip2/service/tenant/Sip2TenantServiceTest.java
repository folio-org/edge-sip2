package org.folio.edge.sip2.service.tenant;

import static org.folio.edge.sip2.domain.TenantResolutionContext.createContextForConnectPhase;
import static org.folio.edge.sip2.domain.TenantResolutionContext.createContextForLoginPhase;
import static org.folio.edge.sip2.domain.type.TenantResolutionPhase.CONNECT;
import static org.folio.edge.sip2.domain.type.TenantResolutionPhase.LOGIN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import io.vertx.core.json.JsonObject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.folio.edge.sip2.domain.ConnectionDetails;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class Sip2TenantServiceTest {

  @Mock private TenantResolver tenantResolver1;
  @Mock private TenantResolver tenantResolver2;

  @BeforeEach
  void setUp() {
    System.setProperty("sip2TenantResolvers", "TEST1,TEST2");
  }

  @AfterEach
  void tearDown() {
    System.clearProperty("sip2TenantResolvers");
  }

  @Test
  void findConfiguration_positive_connectPhase() {
    var connectionDetails = ConnectionDetails.of(6443, "127.0.0.1");
    var sip2Config = sip2Configuration().put("scTenants", List.of(scTenant()));
    var resolutionContext = createContextForConnectPhase(sip2Config, connectionDetails);

    when(tenantResolver1.getPhase()).thenReturn(CONNECT);
    when(tenantResolver1.getName()).thenReturn("TEST1");
    when(tenantResolver2.getName()).thenReturn("TEST2");
    when(tenantResolver1.resolve(resolutionContext)).thenReturn(Optional.of(scTenant()));

    var sip2TenantService = createTestService(tenantResolver1, tenantResolver2);
    var result = sip2TenantService.findConfiguration(resolutionContext);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().getString("tenant"), is("tenant1"));
  }

  @Test
  void findConfiguration_positive_loginPhase() {
    var sip2Config = sip2Configuration().put("scTenants", List.of(scTenant()));
    var sessionData = SessionData.createSession("test", '|', false, "UTF-8");
    var resolutionContext = createContextForLoginPhase(sip2Config, sessionData);

    when(tenantResolver1.getName()).thenReturn("TEST1");
    when(tenantResolver1.getPhase()).thenReturn(CONNECT);

    when(tenantResolver2.getName()).thenReturn("TEST2");
    when(tenantResolver2.getPhase()).thenReturn(LOGIN);
    when(tenantResolver2.resolve(resolutionContext)).thenReturn(Optional.of(scTenant()));

    var sip2TenantService = createTestService(tenantResolver1, tenantResolver2);
    var result = sip2TenantService.findConfiguration(resolutionContext);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().getString("tenant"), is("tenant1"));
  }

  @Test
  void findConfiguration_positive_secondResolverSuccess() {
    var sip2Config = sip2Configuration().put("scTenants", List.of(scTenant()));
    var sessionData = SessionData.createSession("test", '|', false, "UTF-8");
    var resolutionContext = createContextForLoginPhase(sip2Config, sessionData);

    when(tenantResolver1.getName()).thenReturn("TEST1");
    when(tenantResolver1.getPhase()).thenReturn(LOGIN);
    when(tenantResolver1.resolve(resolutionContext)).thenReturn(Optional.empty());

    when(tenantResolver2.getName()).thenReturn("TEST2");
    when(tenantResolver2.getPhase()).thenReturn(LOGIN);
    when(tenantResolver2.resolve(resolutionContext)).thenReturn(Optional.of(scTenant()));

    var sip2TenantService = createTestService(tenantResolver1, tenantResolver2);
    var result = sip2TenantService.findConfiguration(resolutionContext);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().getString("tenant"), is("tenant1"));
  }

  @Test
  void findConfiguration_negative_invalidResolverName() {
    var sip2Config = sip2Configuration().put("scTenants", List.of(scTenant()));
    var sessionData = SessionData.createSession("test", '|', false, "UTF-8");
    var resolutionContext = createContextForLoginPhase(sip2Config, sessionData);

    when(tenantResolver1.getName()).thenReturn("TEST_CUSTOM");

    var sip2TenantService = createTestService(tenantResolver1);
    var result = sip2TenantService.findConfiguration(resolutionContext);

    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void findConfiguration_negative_scTenantsEmpty() {
    var sip2Config = sip2Configuration();
    var sessionData = SessionData.createSession("test", '|', false, "UTF-8");
    var resolutionContext = createContextForLoginPhase(sip2Config, sessionData);

    when(tenantResolver1.getName()).thenReturn("TEST_CUSTOM");

    var sip2TenantService = createTestService(tenantResolver1);
    var result = sip2TenantService.findConfiguration(resolutionContext);

    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void findConfiguration_positive_duplicateResolverNameInProperty() {
    System.setProperty("sip2TenantResolvers", "TEST1,TEST1,TEST1");
    var connectionDetails = ConnectionDetails.of(6443, "127.0.0.1");
    var sip2Config = sip2Configuration().put("scTenants", List.of(scTenant()));
    var resolutionContext = createContextForConnectPhase(sip2Config, connectionDetails);

    when(tenantResolver1.getPhase()).thenReturn(CONNECT);
    when(tenantResolver1.getName()).thenReturn("TEST1");
    when(tenantResolver1.resolve(resolutionContext)).thenReturn(Optional.of(scTenant()));

    var sip2TenantService = createTestService(tenantResolver1);
    var result = sip2TenantService.findConfiguration(resolutionContext);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().getString("tenant"), is("tenant1"));
  }

  private static Sip2TenantService createTestService(TenantResolver... tenantResolvers) {
    return new Sip2TenantService(new LinkedHashSet<>(List.of(tenantResolvers)));
  }

  private static JsonObject sip2Configuration() {
    return new JsonObject()
        .put("tenant", "defaultTenant")
        .put("errorDetectionEnabled", false)
        .put("charset", "UTF-8")
        .put("messageDelimiter", '\r')
        .put("fieldDelimiter", "|");
  }

  private static JsonObject scTenant() {
    return new JsonObject()
        .put("tenant", "tenant1")
        .put("errorDetectionEnabled", false)
        .put("charset", "UTF-8");
  }
}
