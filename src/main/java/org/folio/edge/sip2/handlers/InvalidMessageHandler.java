package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.parser.Command.REQUEST_SC_RESEND;

import freemarker.template.Template;
import io.vertx.core.Future;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.Objects;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;

public class InvalidMessageHandler implements ISip2RequestHandler {

  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(InvalidMessageHandler.class);

  private final Template commandTemplate;

  @Inject
  public InvalidMessageHandler(FreemarkerRepository freemarkerRepository) {
    Objects.requireNonNull(freemarkerRepository, "FreemarkerRepository cannot be null");
    this.commandTemplate = freemarkerRepository.getFreemarkerTemplate(REQUEST_SC_RESEND);
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    log.debug(sessionData, "InvalidMessageHandler :: execute message:{} sessionData:{}",
        message, sessionData);
    final String response = FreemarkerUtils
        .executeFreemarkerTemplate(sessionData, Collections.emptyMap(), commandTemplate);
    log.info(sessionData, "InvalidMessageHandler :: execute commandTemplate:{} response:{}",
        commandTemplate, response);
    return Future.succeededFuture(response);
  }
}
