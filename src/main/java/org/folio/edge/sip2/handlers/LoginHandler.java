package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.FALSE;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.LoginRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.ClientException;


public class LoginHandler implements ISip2RequestHandler {
  private static final Logger log = LogManager.getLogger();

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
    log.debug("LoginHandler :: execute message:{} sessionData:{}",message,sessionData);
    final Login login = (Login) message;

    log.info("LoginHandler :: execute Login: {}", login::getLoginLogInfo);
    Future<Boolean> configFuture;
    if (sessionData.isConfigurationLoaded()) {
      configFuture = Future.succeededFuture(Boolean.TRUE);
    } else {
      configFuture = configurationRepository.getACSStatus(sessionData).map(result -> {
        return Boolean.TRUE;
      }).otherwise(Boolean.FALSE);
    }
    Future<LoginResponse> responseFuture = configFuture.compose( result -> {
      return loginRepository.login(login, sessionData);
    });

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
   * @return
   */
  private String constructLoginResponse(
      SessionData sessionData,
      LoginResponse loginResponse) {
    log.debug("LoginResponse: {}", () -> loginResponse);
    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", sessionData.getFieldDelimiter());
    root.put("loginResponse", loginResponse);

    final String response = FreemarkerUtils
        .executeFreemarkerTemplate(root, commandTemplate);
    log.info("LoginHandler :: execute SIP login response: {}", response);
    return response;
  }

}
