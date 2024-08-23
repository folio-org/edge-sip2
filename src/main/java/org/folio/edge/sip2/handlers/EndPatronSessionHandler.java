package org.folio.edge.sip2.handlers;

import com.google.inject.Inject;
import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.EndPatronSession;
import org.folio.edge.sip2.domain.messages.responses.EndSessionResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.parser.Message;
import org.folio.edge.sip2.repositories.PatronRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.ClientException;

public class EndPatronSessionHandler implements ISip2RequestHandler {

  private static final Logger log = LogManager.getLogger();

  private PatronRepository patronRepository;
  private Template commandTemplate;

  @Inject
  EndPatronSessionHandler(PatronRepository patronRepository,
      @Named("endSessionResponse") Template commandTemplate) {
    this.patronRepository = Objects.requireNonNull(patronRepository,
        "patronRepository cannot be null");
    this.commandTemplate =
      Objects.requireNonNull(commandTemplate, "EndPatronSession template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    log.debug("EndPatronSessionHandler :: execute message:{} sessionData:{}", message,sessionData);

    final EndPatronSession endPatronSession = (EndPatronSession) message;
    log.info("EndPatronSessionHandler :: execute EndPatronSession: {}",
        endPatronSession::getPatronSessionLogInfo);

    final Future<EndSessionResponse> endPatronSessionFuture =
        patronRepository.performEndPatronSessionCommand(endPatronSession, sessionData);

    endPatronSessionFuture.onFailure(throwable -> {
      if (throwable instanceof ClientException) {
        sessionData.setErrorResponseMessage(
            createEndPatronResponse(
              sessionData,
              (EndSessionResponse) sessionData.getErrorResponseMessage()));
      }
    });

    return endPatronSessionFuture.map(endSessionResponse ->
      createEndPatronResponse(
        sessionData,
        endSessionResponse));
  }

  /**
   * Create End Patron Response message.
   * @param sessionData sessionData
   * @param endSessionResponse endSessionResponse
   * @return response String
   */
  private String createEndPatronResponse(
      SessionData sessionData,
      EndSessionResponse endSessionResponse) {

    log.info("EndPatronSessionHandler :: execute EndSessionResponse: {}",
        () -> endSessionResponse);

    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", sessionData.getFieldDelimiter());
    root.put("endSessionResponse", endSessionResponse);
    root.put("timezone", sessionData.getTimeZone());

    final String response = FreemarkerUtils
        .executeFreemarkerTemplate(root, commandTemplate);

    log.info("EndPatronSessionHandler :: execute SIP end session response: {}", response);
    return response;
  }

  @Override
  public void writeHistory(SessionData sessionData, Message<Object> request, String response) {
    //Do not write history for this command
  }
}
