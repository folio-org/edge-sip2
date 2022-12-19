package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.parser.Command.REQUEST_SC_RESEND;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.session.SessionData;

public class InvalidMessageHandler implements ISip2RequestHandler {

  private static final Logger log = LogManager.getLogger();

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    log.debug("InvalidMessageHandler :: execute message:{} sessionData:{}",
        message,sessionData);
    final Template commandTemplate = FreemarkerRepository
        .getInstance().getFreemarkerTemplate(REQUEST_SC_RESEND);
    final String response = FreemarkerUtils
        .executeFreemarkerTemplate(Collections.emptyMap(), commandTemplate);
    log.info("InvalidMessageHandler :: execute commandTemplate:{} response:{}",
        commandTemplate,response);
    return Future.succeededFuture(response);
  }
}
