package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.FALSE;

import freemarker.template.Template;
import io.vertx.core.Future;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.LoginRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.okapi.common.refreshtoken.client.ClientException;


public class LoginHandler implements ISip2RequestHandler {
  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(LoginHandler.class);

  private final ConfigurationRepository configurationRepository;
  private final LoginRepository loginRepository;
  private final Template commandTemplate;

  @Inject
  LoginHandler(LoginRepository loginRepository, ConfigurationRepository configurationRepository,
      @Named("loginResponse") Template commandTemplate) {
    this.loginRepository = Objects.requireNonNull(loginRepository,
        "LoginRepository cannot be null");
    this.configurationRepository = Objects.requireNonNull(configurationRepository,
        "ConfigurationRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    log.debug(sessionData, "LoginHandler :: execute message:{} sessionData:{}",message,sessionData);
    final Login login = (Login) message;

    log.info(sessionData, "LoginHandler :: execute Login: {}", login::getLoginLogInfo);
    log.debug(sessionData, "Session user is {}", sessionData.getUsername());

    Future<LoginResponse> responseFuture = loginRepository.login(login, sessionData)
        .compose(loginResponse ->
            configurationRepository.getACSStatus(sessionData)
            .map(loginResponse)
            .recover(cause -> Future.succeededFuture(loginResponse))
        //Even if it fails, just do the login
        );

    responseFuture.onFailure(e -> {
      if (e instanceof ClientException) {
        sessionData.setErrorResponseMessage(
            constructLoginResponse(
            sessionData,
            LoginResponse.builder().ok(FALSE).build()));
      }
    });

    return responseFuture.compose(loginResponse ->
      Future.succeededFuture(
        constructLoginResponse(
          sessionData, loginResponse)));
  }

  /**
   * Construct Login Response message.
   * @param sessionData sessionData
   * @param loginResponse loginResponse
   * @return response String
   */
  private String constructLoginResponse(SessionData sessionData, LoginResponse loginResponse) {
    log.debug(sessionData, "LoginResponse: {}", () -> loginResponse);
    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", sessionData.getFieldDelimiter());
    root.put("loginResponse", loginResponse);

    final String response = FreemarkerUtils
        .executeFreemarkerTemplate(sessionData, root, commandTemplate);
    log.info(sessionData, "LoginHandler :: execute SIP login response: {}", response);
    return response;
  }

}
