package org.folio.edge.sip2.service.tenant;

import static org.folio.edge.sip2.domain.TenantResolutionContext.createContextForLoginPhase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.json.JsonObject;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class UsernamePrefixTenantResolverTest {

  @InjectMocks private UsernamePrefixTenantResolver tenantResolver;

  @Test
  void resolve_positive() {
    var sessionData = testSessionData("tenant1__user");
    var tenantContext = createContextForLoginPhase(new JsonObject(), sessionData);
    var result = tenantResolver.resolve(tenantContext);

    assertTrue(result.isPresent());
    assertEquals("tenant1", result.get().getString("tenant"));
  }

  @Test
  void resolve_positive_usernameIsNull() {
    var sessionData = testSessionData(null);
    var tenantContext = createContextForLoginPhase(new JsonObject(), sessionData);
    var result = tenantResolver.resolve(tenantContext);

    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_positive_simpleUsername() {
    var sessionData = testSessionData("sample_username");
    var tenantContext = createContextForLoginPhase(new JsonObject(), sessionData);
    var result = tenantResolver.resolve(tenantContext);

    assertTrue(result.isEmpty());
  }

  private static SessionData testSessionData(String username) {
    var sessionData = SessionData.createSession("test", '|', false, "UTF-8");
    sessionData.setUsername(username);
    return sessionData;
  }
}
