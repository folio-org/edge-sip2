package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.repositories.domain.ExtendedUser;
import org.folio.edge.sip2.repositories.domain.PatronPasswordVerificationRecords;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;

/**
 * Provides interaction with the users service.
 *
 * @author mreno-EBSCO
 *
 */
public class UsersRepository {

  private static final Logger log = LogManager.getLogger();

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
  public Future<ExtendedUser> getUserById(
      String identifier,
      SessionData sessionData) {
    Objects.requireNonNull(identifier, "identifier cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");
    log.debug("getUserById identifier:{}", identifier);

    final Map<String, String> headers = getBaseHeaders();

    final GetUserByIdentifierRequestData getUserByBarcodeRequestData =
        new GetUserByIdentifierRequestData(identifier, headers, sessionData);
    final Future<IResource> result = resourceProvider.retrieveResource(getUserByBarcodeRequestData);

    log.debug("Users lookup success is {}", result.succeeded());

    return result
        .otherwise(() -> null)
        .map(IResource::getResource)
        .map(this::getUserFromList)
        .compose(user -> {
          Future<IResource> blResult;
          if (user != null) {
            log.debug("Getting extended user info for id {}", user.getId());
            final GetExtendedUserData getExtendedUserData =
                new GetExtendedUserData(user.getId(), headers, sessionData);
            log.debug("Path for extended user lookup is {}", getExtendedUserData.getPath());
            blResult = resourceProvider.retrieveResource(getExtendedUserData);
          } else {
            blResult = Future.failedFuture("Invalid User");
          }
          return blResult
            .otherwise(() -> null)
            .compose(extendedUserResult -> {
              if (blResult.failed()) {
                return Future.succeededFuture(null);
              } else {
                JsonObject extendedUserJson = extendedUserResult.getResource();
                log.debug("Got extended user JSON: {}", extendedUserJson.encode());
                JsonObject patronGroupJson = extendedUserJson.getJsonObject("patronGroup");
                ExtendedUser extendedUser = new ExtendedUser();
                extendedUser.setUser(user);
                if (patronGroupJson != null) {
                  extendedUser.setPatronGroup(
                      patronGroupJson.getString("group"),
                      patronGroupJson.getString("desc"),
                      patronGroupJson.getString("id")
                  );
                }
                return Future.succeededFuture(extendedUser);
              }
            });
        });
  }

  private User getUserFromList(JsonObject userList) {
    log.info("getUserFromList userList:{}",userList);
    final User user;

    if (userList == null
        || userList.getInteger("totalRecords", 0) == 0) {
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

  private class GetExtendedUserData implements IRequestData {
    private final String userId;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private GetExtendedUserData(String userId, Map<String, String> headers,
        SessionData sessionData) {
      this.userId = userId;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      return "/bl-users/by-id/" + userId;
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
      StringBuilder query = new StringBuilder()
          .append("(barcode==")
          .append(identifier)
          .append(" or externalSystemId==")
          .append(identifier)
          .append(" or username==")
          .append(identifier)
          .append(')');
      return "/users?limit=1&query=" + Utils.encode(query.toString());
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

  private class PinVerifyRequestData implements IRequestData {
    private final String userId;
    private final String pin;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private PinVerifyRequestData(
        String userId,
        String pin,
        Map<String, String> headers,
        SessionData sessionData) {
      this.userId = userId;
      this.pin = pin;
      this.headers = headers;
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      return "/patron-pin/verify";
    }

    @Override
    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }

    @Override
    public JsonObject getBody() {
      JsonObject body = new JsonObject();
      body.put("id", userId);
      body.put("pin", pin);

      return body;
    }

  }

  private Map<String, String> getBaseHeaders() {
    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");
    return headers;
  }

  private Map<String, String> getAcceptTextHeaders() {
    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "text/plain");
    return headers;
  }

  /**
   * Check to see if a given userId/pin combo is valid or not.
   * @param userId The user id
   * @param pin The pin
   * @param sessionData Current sessiondata
   * @return Boolean true or false
   */
  public Future<Boolean> checkPatronPin(String userId, String pin, SessionData sessionData) {
    final Map<String, String> headers = getBaseHeaders();
    PinVerifyRequestData pinVerifyRequestData
        = new PinVerifyRequestData(userId, pin, headers, sessionData);
    Future<IResource> pinResult = resourceProvider.createResource(pinVerifyRequestData);
    return pinResult
      .otherwise(() -> null)
      .compose(pinCheckResult -> {
        if (pinResult.failed()) {
          return Future.succeededFuture(Boolean.FALSE);
        } else {
          return Future.succeededFuture(Boolean.TRUE);
        }
      }
    );

  }

  /**
   * Verify a patron against a provided pin (if verification is required) and return a
   * PatronPasswordVerificationRecords future.
   * @param identifier The user barcode
   * @param pin The user pin
   * @param sessionData Current sessiondata
   * @return PatronPasswordVerificationRecords future indicating whether or not verification was
   *         successful
   */
  public Future<PatronPasswordVerificationRecords> verifyPatronPin(
      String identifier,
      String pin,
      SessionData sessionData
  ) {
    final Future<PatronPasswordVerificationRecords> verifyFuture;
    if (sessionData.isPatronPasswordVerificationRequired()) {
      verifyFuture = doPatronPinVerification(identifier, pin, sessionData);
    } else {
      verifyFuture = getUserById(identifier, sessionData).compose(extendedUser -> {
        if (extendedUser != null) {
          return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
              .extendedUser(extendedUser).build());
        }
        return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
            .build()); //null user
      });
    }
    return verifyFuture;
  }


  /**
   * Verify a patron against a provided pin and return a PatronPasswordVerificationRecords future.
   * @param identifier The user barcode
   * @param pin The user pin
   * @param sessionData Current sessiondata
   * @return PatronPasswordVerificationRecords future indicating whether or not verification was
   *         successful
   */
  public Future<PatronPasswordVerificationRecords> doPatronPinVerification(
      String identifier,
      String pin,
      SessionData sessionData) {
    final Map<String, String> headers = getAcceptTextHeaders();

    return getUserById(identifier, sessionData).compose(extendedUser -> {
      if (extendedUser == null) {
        return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
          .passwordVerified(Boolean.FALSE)
          .build());
      }
      PinVerifyRequestData pinVerifyRequestData
          = new PinVerifyRequestData(extendedUser.getUser().getId(), pin, headers, sessionData);
      log.debug("Attempting to verify pin");
      Future<Boolean> pinResult = resourceProvider.doPinCheck(pinVerifyRequestData);
      return pinResult
        .compose(pinCheckResult -> {
          return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
            .extendedUser(extendedUser)
            .passwordVerified(Boolean.TRUE)
            .build());
        }, throwable -> { //Return false on error
            log.debug("Error on pin check");
            return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
                .extendedUser(extendedUser)
                .errorMessages(Collections.singletonList("Error verifying PIN"))
                .passwordVerified(Boolean.FALSE)
                .build());
          });
    });
  }
}
