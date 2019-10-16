package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;

/**
 * Provides interaction with the users service.
 *
 * @author mreno-EBSCO
 *
 */
public class UsersRepository {
  private final IResourceProvider<IRequestData> resourceProvider;

  @Inject
  UsersRepository(IResourceProvider<IRequestData> resourceProvider) {
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "Resource provider cannot be null");
  }

  /**
   * Get user data by the user's barcode.
   *
   * @param identifier the user's identifier, which can be barcode, external system ID, or username
   * @param sessionData session data
   * @return the user details in raw JSON
   */
  public Future<User> getUserById(
      String identifier,
      SessionData sessionData) {
    Objects.requireNonNull(identifier, "identifier cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");

    final GetUserByIdentifierRequestData getUserByBarcodeRequestData =
        new GetUserByIdentifierRequestData(identifier, headers, sessionData);
    final Future<IResource> result = resourceProvider.retrieveResource(getUserByBarcodeRequestData);

    return result
        .otherwise(() -> null)
        .map(IResource::getResource)
        .map(this::getUserFromList);
  }

  private User getUserFromList(JsonObject userList) {
    final User user;

    if (userList == null ||
      userList.getInteger("totalRecords", 0) == 0) {
      user = null;
    } else {
      final JsonArray users = userList.getJsonArray("users");
      if (users == null || users.size() == 0) {
        user = null;
      } else {
        // there should be only 1 user, if barcode/username/the external ID exists
        user = users.getJsonObject(0).mapTo(User.class);
      }
    }

    return user;
  }

  private class GetUserByIdentifierRequestData implements IRequestData {
    private final String identifier;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private GetUserByIdentifierRequestData(String identifier, Map<String, String> headers,
        SessionData sessionData) {
      this.identifier = identifier;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      return "/users?limit=1&query=(barcode==" + identifier
                    + " or externalSystemId==" + identifier
                    + " or username==" + identifier +')';
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
