package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.parser.Command.REQUEST_SC_RESEND;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.Collections;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.session.SessionData;

public class InvalidMessageHandler implements ISip2RequestHandler {
  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    final Template commandTemplate = FreemarkerRepository
        .getInstance().getFreemarkerTemplate(REQUEST_SC_RESEND);
    final String response = FreemarkerUtils
        .executeFreemarkerTemplate(Collections.emptyMap(), commandTemplate);
    return Future.succeededFuture(response);
  }
}
