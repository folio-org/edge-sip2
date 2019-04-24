package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
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
   * @param barcode the user's barcode
   * @param sessionData session data
   * @return the user details in raw JSON
   */
  public Future<JsonObject> getUserByBarcode(
      String barcode,
      SessionData sessionData) {
    Objects.requireNonNull(barcode, "barcode cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");

    final GetUserByBarcodeRequestData getUserByBarcodeRequestData =
        new GetUserByBarcodeRequestData(barcode, headers, sessionData);
    final Future<IResource> result = resourceProvider.retrieveResource(getUserByBarcodeRequestData);

    return result
        .otherwise(() -> null)
        .map(IResource::getResource)
        .compose(this::getUserFromList);
  }

  private Future<JsonObject> getUserFromList(JsonObject userList) {
    final JsonObject user;

    if (userList == null || userList.getInteger("totalRecords",
        Integer.valueOf(0)).intValue() == 0) {
      user = null;
    } else {
      final JsonArray users = userList.getJsonArray("users");
      if (users == null || users.size() == 0) {
        user = null;
      } else {
        // there should be only 1 user, if the barcode exists
        user = users.getJsonObject(0);
      }
    }

    return Future.succeededFuture(user);
  }

  private class GetUserByBarcodeRequestData implements IRequestData {
    private final String barcode;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private GetUserByBarcodeRequestData(String barcode, Map<String, String> headers,
        SessionData sessionData) {
      this.barcode = barcode;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      return "/users?limit=1&query=barcode%3D%3D" + barcode;
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
