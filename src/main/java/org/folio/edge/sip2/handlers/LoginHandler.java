package org.folio.edge.sip2.handlers;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.LoginRepository;

public class LoginHandler implements ISip2RequestHandler {
  private static final Logger log = LogManager.getLogger();

  private final LoginRepository loginRepository;
  private final Template commandTemplate;

  /**
   * Construct a Login handler with the specified parameters.
   * @param loginRepository the login repository
   * @param commandTemplate the command template
   */
  public LoginHandler(
      LoginRepository loginRepository,
      Template commandTemplate) {
    this.loginRepository = Objects.requireNonNull(loginRepository,
        "LoginRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate,
        "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message) {
    final Login login = (Login) message;

    log.debug("Login: {}", () -> login);

    final Future<LoginResponse> responseFuture = loginRepository.login(login);

    return responseFuture.compose(loginResponse -> {
      log.debug("LoginResponse: {}", () -> loginResponse);
  
      final Map<String, Object> root = new HashMap<>();
      root.put("formatDateTime", new FormatDateTimeMethodModel());
      root.put("delimiter", "|");
      root.put("loginResponse", loginResponse);

      final String response = FreemarkerUtils
          .executeFreemarkerTemplate(root, commandTemplate);

      log.debug("SIP login response: {}", response);

      return Future.succeededFuture(response);
    });
  }
}
