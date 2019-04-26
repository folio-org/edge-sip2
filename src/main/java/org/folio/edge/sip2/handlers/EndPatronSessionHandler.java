package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.parser.Command.END_PATRON_SESSION;

import freemarker.template.Template;
import io.vertx.core.Future;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.EndPatronSession;
import org.folio.edge.sip2.domain.messages.responses.EndSessionResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.session.SessionData;

public class EndPatronSessionHandler implements ISip2RequestHandler {

  private static final Logger log = LogManager.getLogger();

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {

    final EndPatronSession endPatronSession = (EndPatronSession) message;
    log.debug("EndPatronSession: {}", () -> endPatronSession);

    sessionData.setAuthenticationToken(null);
    sessionData.setUsername(null);
    sessionData.setPassword(null);


    final Future<EndSessionResponse> endSessionResponseFuture = null;


    return endSessionResponseFuture.compose(checkinResponse -> {
      log.debug("EndPatronSession: {}", () -> checkinResponse);

      EndSessionResponse endSessionResponse = EndSessionResponse.builder()
                                      .endSession(true)
                                      .institutionId(endPatronSession.getInstitutionId())
                                      .patronIdentifier(endPatronSession.getPatronIdentifier())
                                      .printLine("")
                                      .screenMessage("")
                                      .transactionDate(ZonedDateTime.now())
                                      .build();


      Template commandTemplate = FreemarkerRepository
                                    .getInstance()
                                    .getFreemarkerTemplate(END_PATRON_SESSION);


      final Map<String, Object> root = new HashMap<>();
      root.put("formatDateTime", new FormatDateTimeMethodModel());
      root.put("delimiter", sessionData.getFieldDelimiter());
      root.put("endPatronSessionResponse", endSessionResponse);

      final String response = FreemarkerUtils
          .executeFreemarkerTemplate(root, commandTemplate);

      log.debug("SIP checkin response: {}", response);
      return Future.succeededFuture(response);
    });
  }
}
