package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.lang.String;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Clock;
import java.time.OffsetDateTime;
// import java.time.ZoneOffset;
// import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;  
import java.util.regex.Pattern;  
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.folio.edge.sip2.domain.messages.responses.FeePaidResponse;
// import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;

/**
 * Provides interaction with the feefines service.
 *
 * @author mreno-EBSCO
 *
 */
public class FeeFinesRepository {
  private static final Logger log = LogManager.getLogger();
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
    headers.put("accept", "application/json");
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
    headers.put("accept", "application/json");

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
    headers.put("accept", "application/json");

    final FeePaymentAccountsRequestData getFeePaymentAccountsRequestData =
        new FeePaymentAccountsRequestData(userId, headers, sessionData);
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
    headers.put("accept", "application/json");

    final GetAccountByUserIdRequestData getAccountByUserIdRequestData =
        new GetAccountByUserIdRequestData(userId, headers, sessionData);
    final Future<IResource> result =
        resourceProvider.retrieveResource(getAccountByUserIdRequestData);

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
      return "/manualblocks?query=" + Utils.encode("userId==" + userId);
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }

  private class FeePaymentAccountsRequestData implements IRequestData {

    private String userId;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private FeePaymentAccountsRequestData(
        String userId,
        Map<String, String> headers,
        SessionData sessionData) {
      this.userId = userId;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }
    
    public String getPath() {
      String uri = "/accounts?query="
          + Utils.encode("(userId==" + this.userId + "  and status.name==Open)");
      return uri;
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }

  private class FeePaymentRequestData implements IRequestData {

    private final String amount;
    private final Boolean notifyPatron;
    private final String paymentMethod;
    private String account;
    private List<String> accounts;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private FeePaymentRequestData(
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
    
    public String getPath() {
      if (account.equals("")) {
        return "/accounts-bulk/pay";
      }

      return "/accounts/" + account + "/pay";
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public JsonObject getBody() {
      JsonObject body = new JsonObject();
      body
          .put("amount", amount)
          //.put("comments", itemIdentifier)
          //.put("transactionInfo", feeIdentifier
          .put("notifyPatron", notifyPatron)
          .put("servicePointId", sessionData.getScLocation())
          .put("userName", sessionData.getUsername())
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

  private class GetAccountByUserIdRequestData implements IRequestData {
    private final String userId;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private GetAccountByUserIdRequestData(String userId, Map<String, String> headers,
                                               SessionData sessionData) {
      this.userId = userId;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      final StringBuilder qSb = new StringBuilder()
          .append("/accounts?query=")
          .append("(userId==")
          .append(userId).append(")").append("&limit=1000");
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


  

  /**
   * Perform a feePaid.
   *
   * @param feePaid the feePaid domain object
   * @return the feePaid response domain object
   */
  public Future<FeePaidResponse> performFeePaidCommand(FeePaid feePaid, SessionData sessionData) {
    // We'll need to convert this date properly. It is likely that it will not include timezone
    // information, so we'll need to use the tenant/SC timezone as the basis and convert to UTC.
    // final String scLocation = sessionData.getScLocation();
    NumberFormat moneyFormatter = new DecimalFormat("0.00");
    
    final String institutionId = feePaid.getInstitutionId();
    final String patronIdentifier = feePaid.getPatronIdentifier();
    final String transactionId = feePaid.getTransactionId();
    
    final Float amountPaid = Float.valueOf(feePaid.getFeeAmount()); //TODO - Decimal, not Float?
    String feeIdentifierMatch = "";
    if (feePaid.getFeeIdentifier() != null) {
      Pattern startWithUuid = Pattern.compile("^(\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12})"); 
      // UUID of the account to be paid
      Matcher matcher = startWithUuid.matcher(feePaid.getFeeIdentifier());  
      feeIdentifierMatch = "";
      if (matcher.find()) { 
        feeIdentifierMatch = matcher.group(1);
      }
    }
    final String feeIdentifier = feeIdentifierMatch;
    log.debug("feeIdentifier = " + feeIdentifier);

    // This may need to be changed to passwordVerifier - GDG
    return usersRepository.getUserById(patronIdentifier, sessionData)
      .compose(user -> { 

        final Map<String, String> acctheaders = getBaseHeaders();

        FeePaymentAccountsRequestData feePaymentAccountsRequestData =
            new FeePaymentAccountsRequestData(user.getId(), acctheaders, sessionData);

        Future<IResource> result;
        result = resourceProvider
            .retrieveResource(feePaymentAccountsRequestData);

        return result
            .otherwiseEmpty()
            .compose(resource -> {
              JsonObject accts = resource.getResource();
              final JsonArray acctList = accts.getJsonArray("accounts");
              Float acctTotal = totalAmount(acctList);
              BigDecimal bdAmountPaid = new BigDecimal(moneyFormatter.format(amountPaid));
              BigDecimal bdAmountTotal = new BigDecimal(moneyFormatter.format(acctTotal));
              log.debug("bdAmountPaid = " + bdAmountPaid);
              log.debug("bdAmountTotal = " + bdAmountTotal);
              log.debug("Amount difference = " + bdAmountPaid.compareTo(bdAmountTotal));
              // On overpayment return a FALSE Payment Accepted
              if (bdAmountPaid.compareTo(bdAmountTotal) > 0) {
                List<String> scrnMsg = List.of("Paid amount ($"
                    + moneyFormatter.format(amountPaid) + ") is more than amount owed ($"
                    + moneyFormatter.format(acctTotal)
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

              Future<IResource> payresult;
              payresult = resourceProvider
                .createResource(feePaymentRequestData);
              
              return payresult
                .otherwiseEmpty()
                .compose(payresource -> {
                  JsonObject paidResponse = payresource.getResource();
                  log.debug("paidResponse = " + paidResponse.encode());
                  return Future.succeededFuture(FeePaidResponse.builder()
                    .paymentAccepted(paidResponse == null ? FALSE : TRUE)
                    .transactionDate(OffsetDateTime.now(clock))
                    .transactionId(transactionId)
                    .institutionId(institutionId)
                    .patronIdentifier(patronIdentifier)
                    .screenMessage(Optional.of(resource.getErrorMessages())
                        .filter(v -> !v.isEmpty())
                        .orElse(null))
                    .build());
                });
            });
      });
  }

  private Float totalAmount(JsonArray arr) {
    Float total = 0.0f;
    for (int i = 0;i < arr.size();i++) {
      total += arr.getJsonObject(i).getFloat("remaining");
    }
    return total;
  }

  private List<String> getAcctIdList(JsonArray arr) {
    List<String> list = new ArrayList<String>();
    for (int i = 0;i < arr.size();i++) {
      list.add(arr.getJsonObject(i).getString("id"));
    }
    return list;
  }
}
