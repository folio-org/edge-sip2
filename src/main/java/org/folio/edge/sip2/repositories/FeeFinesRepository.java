package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.folio.edge.sip2.session.SessionData;

/**
 * Provides interaction with the feefines service.
 *
 * @author mreno-EBSCO
 *
 */
public class FeeFinesRepository {
  private final IResourceProvider<IRequestData> resourceProvider;

  @Inject
  FeeFinesRepository(IResourceProvider<IRequestData> resourceProvider) {
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "Resource provider cannot be null");
  }

  /**
   * Get a patron's manual blocks.
   *
   * @param userId the user's ID
   * @param sessionData session data
   * @return the manual blocks list in raw JSON
   */
  public Future<JsonObject> getManualBlocksByUserId(
      String userId,
      SessionData sessionData) {
    Objects.requireNonNull(userId, "userId cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");

    final GetManualBlocksByUserIdRequestData getManualBlocksByUserIdRequestData =
        new GetManualBlocksByUserIdRequestData(userId, headers, sessionData);
    final Future<IResource> result =
        resourceProvider.retrieveResource(getManualBlocksByUserIdRequestData);

    return result
        .otherwise(() -> null)
        .map(IResource::getResource);
  }

  private class GetManualBlocksByUserIdRequestData implements IRequestData {
    private final String userId;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private GetManualBlocksByUserIdRequestData(String barcode, Map<String, String> headers,
        SessionData sessionData) {
      this.userId = barcode;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      return "/manualblocks?query=userId==" + userId;
    }

    @Override
    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }
}
