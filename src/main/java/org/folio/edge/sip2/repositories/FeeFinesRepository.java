package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.PatronAccountInfo;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.folio.edge.sip2.domain.messages.responses.FeePaidResponse;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;
import org.folio.util.PercentCodec;
import org.folio.util.StringUtil;

/**
 * Provides interaction with the feefines service.
 *
 * @author mreno-EBSCO
 *
 */
public class FeeFinesRepository {
  private static final Logger log = LogManager.getLogger();
  private static final String HEADER_ACCEPT = "accept";
  private static final String MIMETYPE_JSON = "application/json";
  private static final String ACCOUNTS_KEY = "accounts";
  private static final String ACCOUNTS_QUERY_URL = "/accounts?query=";
  private static final String LIMIT_PARAM = "&limit=1000";
  private final IResourceProvider<IRequestData> resourceProvider;
  private final UsersRepository usersRepository;
  private Clock clock;


  @Inject
  FeeFinesRepository(IResourceProvider<IRequestData> resourceProvider,
      UsersRepository usersRepository,
      Clock clock) {
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "Resource provider cannot be null");
    this.usersRepository = Objects.requireNonNull(usersRepository,
        "UsersRepository cannot be null");
    this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
  }

  private Map<String, String> getBaseHeaders() {
    final Map<String, String> headers = new HashMap<>();
    headers.put(HEADER_ACCEPT, MIMETYPE_JSON);
    return headers;
  }

  /**
   * Get a patron's manual blocks.
   *
   * @param userId the user's ID
   * @param sessionData session data
   * @return the manual blocks list in raw JSON or {@code null} if there was an error
   */
  public Future<JsonObject> getManualBlocksByUserId(
      String userId,
      SessionData sessionData) {
    Objects.requireNonNull(userId, "userId cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final Map<String, String> headers = new HashMap<>();
    headers.put(HEADER_ACCEPT, MIMETYPE_JSON);

    final GetManualBlocksByUserIdRequestData getManualBlocksByUserIdRequestData =
        new GetManualBlocksByUserIdRequestData(userId, headers, sessionData);
    final Future<IResource> result =
        resourceProvider.retrieveResource(getManualBlocksByUserIdRequestData);

    return result
        .otherwise(() -> null)
        .map(IResource::getResource);
  }




  /**
   * Get a patron's total fee amount.
   *
   * @param userId the user's ID
   * @param sessionData session data
   * @return the accounts list in raw JSON or {@code null} if there was an error
   */
  public Future<JsonObject> getFeeAmountByUserId(
      String userId,
      SessionData sessionData) {
    Objects.requireNonNull(userId, "userId cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final Map<String, String> headers = new HashMap<>();
    headers.put(HEADER_ACCEPT, MIMETYPE_JSON);

    final FeePaymentAccountsRequestData getFeePaymentAccountsRequestData =
        new FeePaymentAccountsRequestData(userId, headers, null, sessionData);
    final Future<IResource> result =
        resourceProvider.retrieveResource(getFeePaymentAccountsRequestData);

    return result
        .otherwise(() -> null)
        .map(IResource::getResource);
  }


  /**
   * Get a patron's account.
   *
   * @param userId the user's ID
   * @param sessionData session data
   * @return the account data list in raw JSON or {@code null} if there was an error
   */
  public Future<JsonObject> getAccountDataByUserId(
      String userId,
      SessionData sessionData) {
    Objects.requireNonNull(userId, "userId cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final Map<String, String> headers = new HashMap<>();
    headers.put(HEADER_ACCEPT, MIMETYPE_JSON);

    final GetAccountByUserIdRequestData getAccountByUserIdRequestData =
        new GetAccountByUserIdRequestData(userId, headers, sessionData);
    final Future<IResource> result =
        resourceProvider.retrieveResource(getAccountByUserIdRequestData);

    return result
      .otherwise(() -> null)
      .map(IResource::getResource)
      .compose(accountJson -> {
        List<String> idList = getFeeFineIdList(accountJson);
        return getFeeFinesByIds(idList, sessionData)
            .compose(feeFinesJson ->
                Future.succeededFuture(populateFeeFinesDetails(accountJson, feeFinesJson))
            );
      });
  }

  /**
   * Get a listing of feeFines objects.
   *
   * @param ids a list of UUID strings
   * @param sessionData session data
   * @return a JsonObject Future of the FeeFine response
   */
  public Future<JsonObject> getFeeFinesByIds(List<String> ids, SessionData sessionData) {
    Objects.requireNonNull(ids, "ids cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final Map<String, String> headers = new HashMap<>();
    headers.put(HEADER_ACCEPT, MIMETYPE_JSON);

    final GetFeeFinesByIdsRequestData getFeeFinesByIdsRequestData =
        new GetFeeFinesByIdsRequestData(ids, headers, sessionData);
    final Future<IResource> result =
        resourceProvider.retrieveResource(getFeeFinesByIdsRequestData);

    return result
      .otherwise(() -> null)
      .map(IResource::getResource);
  }

  protected static class GetManualBlocksByUserIdRequestData implements IRequestData {
    private final String userId;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    protected GetManualBlocksByUserIdRequestData(String barcode, Map<String, String> headers,
        SessionData sessionData) {
      this.userId = barcode;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }



    @Override
    public String getPath() {
      return "/manualblocks?query=" + PercentCodec.encode(cqlUserId(userId));
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

  protected static class FeePaymentAccountsRequestData implements IRequestData {

    private String userId;
    private final Map<String, String> headers;
    private final String accountIdentifier;
    private final SessionData sessionData;

    protected FeePaymentAccountsRequestData(
        String userId,
        Map<String, String> headers,
        String accountIdentifier,
        SessionData sessionData) {
      this.userId = userId;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.accountIdentifier = accountIdentifier;
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      var cql = cqlUserId(userId) + " and status.name==\"Open\"";
      if (!Utils.isStringNullOrEmpty(accountIdentifier)) {
        cql += " and id==" + StringUtil.cqlEncode(accountIdentifier);
      }
      return ACCOUNTS_QUERY_URL
          + PercentCodec.encode(cql)
          + LIMIT_PARAM;
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

  protected static class FeePaymentRequestData implements IRequestData {

    private final String amount;
    private final Boolean notifyPatron;
    private final String paymentMethod;
    private String userName;
    private String account;
    private List<String> accounts;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    protected FeePaymentRequestData(
        String amount,
        String paymentMethod,
        Boolean notifyPatron,
        String account,
        List<String> accounts,
        Map<String, String> headers,
        SessionData sessionData) {
      this.amount = amount.trim();
      this.notifyPatron = notifyPatron;
      this.paymentMethod = paymentMethod;
      this.account = account;
      this.accounts = accounts;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    public void setUserName(String userName) {
      this.userName = userName;
    }

    @Override
    public String getPath() {
      if (account.equals("")) {
        return "/accounts-bulk/pay";
      }

      return "/accounts/" + account + "/pay";
    }

    @Override
    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public JsonObject getBody() {
      JsonObject body = new JsonObject();
      body
          .put("amount", amount)
          .put("notifyPatron", notifyPatron)
          .put("servicePointId", sessionData.getScLocation())
          .put("userName", userName)
          .put("paymentMethod", paymentMethod);
      if (account.equals("")) {
        body.put("accountIds", new JsonArray(accounts));
      }
      return body;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }

  protected static class GetAccountByUserIdRequestData implements IRequestData {
    private final String userId;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    protected GetAccountByUserIdRequestData(String userId, Map<String, String> headers,
                                               SessionData sessionData) {
      this.userId = userId;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      final StringBuilder qSb = new StringBuilder()
          .append(ACCOUNTS_QUERY_URL)
          .append(PercentCodec.encode(cqlUserId(userId)))
          .append(LIMIT_PARAM);
      return qSb.toString();
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

  protected static class GetFeeFinesByIdsRequestData implements IRequestData {

    private List<String> idList;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    protected GetFeeFinesByIdsRequestData(List<String> idList, Map<String, String> headers,
        SessionData sessionData) {
      this.idList = idList;
      this.headers = headers;
      this.sessionData = sessionData;
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
    public String getPath() {
      var cql = idList.stream()
          .map(StringUtil::cqlEncode)
          .collect(Collectors.joining(" or ", "id==(", ")"));
      return "/feefines?query=" + PercentCodec.encodeAsString(cql);
    }
  }



  /**
   * Perform a feePaid.
   *
   * @param feePaid the feePaid domain object
   * @return the feePaid response domain object
   */
  public Future<FeePaidResponse> performFeePaidCommand(FeePaid feePaid, SessionData sessionData) {
    // We'll need to convert this date properly. It is likely that it will not include timezone
    // information, so we'll need to use the tenant/SC timezone as the basis and convert to UTC.
    final MathContext moneyFormat = new MathContext(2);

    final String institutionId = feePaid.getInstitutionId();
    final String patronIdentifier = feePaid.getPatronIdentifier();
    final String transactionId = feePaid.getTransactionId();

    List<PatronAccountInfo> patronAccountInfoList = new ArrayList<>();

    String feeIdentifierMatch = matchUuid(feePaid.getFeeIdentifier());

    final String feeIdentifier = feeIdentifierMatch;
    log.debug("feeIdentifier = {}", feeIdentifier);

    // This may need to be changed to passwordVerifier - GDG
    return usersRepository.getUserById(patronIdentifier, sessionData)
      .compose(extendedUser -> {
        User user = extendedUser.getUser();
        final Map<String, String> acctheaders = getBaseHeaders();

        FeePaymentAccountsRequestData feePaymentAccountsRequestData =
            new FeePaymentAccountsRequestData(user.getId(), acctheaders,
                feeIdentifier, sessionData);

        Future<IResource> userAccountDataResult;
        userAccountDataResult = resourceProvider
            .retrieveResource(feePaymentAccountsRequestData);

        return userAccountDataResult
            .otherwiseEmpty()
            .compose(resource -> {
              final BigDecimal amountPaid = new BigDecimal(feePaid.getFeeAmount(), moneyFormat);
              JsonObject accts = resource.getResource();
              final JsonArray acctList = accts.getJsonArray(ACCOUNTS_KEY);
              final BigDecimal amountTotal = totalAmount(acctList).round(moneyFormat);
              log.debug("bdAmountPaid = {}", amountPaid);
              log.debug("bdAmountTotal = {}", amountTotal);
              log.debug("Amount difference = {}", amountPaid.compareTo(amountTotal));
              // On overpayment return a FALSE Payment Accepted
              if (amountPaid.compareTo(amountTotal) > 0) {
                List<String> scrnMsg = List.of("Paid amount ($"
                    + amountPaid.toPlainString() + ") is more than amount owed ($"
                    + amountTotal.toPlainString()
                    + "). Please limit payment to no more than the amount owed.");
                return Future.succeededFuture(FeePaidResponse.builder()
                .paymentAccepted(FALSE)
                .transactionDate(OffsetDateTime.now(clock))
                .transactionId(transactionId)
                .institutionId(institutionId)
                .patronIdentifier(patronIdentifier)
                .screenMessage(scrnMsg)
                .build());
              }

              final Map<String, String> headers = getBaseHeaders();

              for (Object accountOb : acctList) {
                JsonObject accountJson = (JsonObject) accountOb;
                PatronAccountInfo patronAccountInfo = new PatronAccountInfo();
                patronAccountInfo.setId(accountJson.getString("id"));
                patronAccountInfo.setItemBarcode(accountJson.getString("barcode"));
                patronAccountInfo.setFeeFineId(accountJson.getString("feeFineId"));
                patronAccountInfo.setFeeFineAmount(accountJson.getDouble("amount"));
                patronAccountInfoList.add(patronAccountInfo);
              }

              List<String> acctIdList = getAcctIdList(acctList);

              FeePaymentRequestData feePaymentRequestData =
                  new FeePaymentRequestData(
                  feePaid.getFeeAmount(),
                  "Credit Card", // TODO - Default PaymentMethod
                  TRUE, // TODO - Default Notify
                  feeIdentifier,
                  acctIdList,
                  headers,
                  sessionData);
              
              feePaymentRequestData.setUserName(sessionData.getUsername());

              log.debug("Json for payment request is {}", feePaymentRequestData.getBody().encode());

              Future<IResource> payresult;
              payresult = resourceProvider
                .createResource(feePaymentRequestData);

              return payresult
                .otherwiseEmpty()
                .compose(payresource -> {
                  JsonObject paidResponse = payresource.getResource();
                  updatePatronAccountInfoList(patronAccountInfoList, paidResponse);
                  log.debug("paidResponse = {}",
                      paidResponse != null ? paidResponse.encode() : "null");
                  return Future.succeededFuture(FeePaidResponse.builder()
                    .paymentAccepted(paidResponse == null ? FALSE : TRUE)
                    .transactionDate(OffsetDateTime.now(clock))
                    .transactionId(transactionId)
                    .institutionId(institutionId)
                    .patronIdentifier(patronIdentifier)
                    .screenMessage(Optional.of(resource.getErrorMessages())
                        .filter(v -> !v.isEmpty())
                        .orElse(null))
                    .patronAccountInfoList(patronAccountInfoList)
                    .build());
                });
            });
      });
  }

  private static BigDecimal totalAmount(JsonArray arr) {
    BigDecimal total = BigDecimal.ZERO;
    for (int i = 0;i < arr.size();i++) {
      total = total.add(BigDecimal.valueOf(arr.getJsonObject(i).getDouble("remaining")));
    }
    return total;
  }

  private List<String> getAcctIdList(JsonArray arr) {
    List<String> list = new ArrayList<>();
    for (int i = 0;i < arr.size();i++) {
      list.add(arr.getJsonObject(i).getString("id"));
    }
    return list;
  }

  private List<String> getFeeFineIdList(JsonObject accountJson) {
    List<String> idList = new ArrayList<>();
    JsonArray accountArray = accountJson.getJsonArray(ACCOUNTS_KEY);
    if (accountArray != null) {
      for (Object ob : accountArray) {
        String id = ((JsonObject)ob).getString("id");
        idList.add(id);
      }
    }
    return idList;
  }

  private JsonObject populateFeeFinesDetails(JsonObject accountJson, JsonObject feeFinesJson) {
    if (accountJson != null && feeFinesJson != null) {
      JsonArray accountArray = accountJson.getJsonArray(ACCOUNTS_KEY);
      JsonArray feeFinesArray = feeFinesJson.getJsonArray("feefines");
      if (feeFinesArray != null && accountArray != null) {
        for (Object ob : accountArray) {
          JsonObject account = (JsonObject)ob;
          String feeFineId = account.getString("feeFineId","");
          for (Object ob2 : feeFinesArray) {
            JsonObject feeFine = (JsonObject)ob2;
            if (feeFineId.equals(feeFine.getString("id"))) {
              account.put("feeFineType", feeFine.getString("feeFineType"));
              break;
            }
          }
        }
      }
    }
    return accountJson;
  }

  private void updatePatronAccountInfoList(List<PatronAccountInfo> patronAccountInfoList,
      JsonObject feePaidResponseJson) {
    if (feePaidResponseJson == null) {
      return;
    }
    JsonArray feeFineActionsArray = feePaidResponseJson.getJsonArray("feefineactions");
    if (feeFineActionsArray != null) {
      for (Object ob : feeFineActionsArray) {
        JsonObject actionJson = (JsonObject)ob;
        String accountId = actionJson.getString("accountId");
        for (PatronAccountInfo patronAccountInfo : patronAccountInfoList) {
          if (patronAccountInfo.getId().equals(accountId)) {
            patronAccountInfo.setFeeFineRemaining(actionJson.getDouble("balance"));
            patronAccountInfo.setFeeFinePaid(actionJson.getDouble("amountAction"));
            break;
          }
        }
      }
    }
  }

  protected static String matchUuid(String identifier) {
    String uuidMatch = "";
    if (identifier != null) {
      Pattern startWithUuid = Pattern.compile("^(\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12})");
      // UUID of the account to be paid
      Matcher matcher = startWithUuid.matcher(identifier);
      if (matcher.find()) {
        uuidMatch = matcher.group(1);
      }
    }
    return uuidMatch;
  }

  private static String cqlUserId(String userId) {
    return "userId==" + StringUtil.cqlEncode(userId);
  }
}
