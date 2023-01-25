package org.folio.edge.sip2.handlers;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.ItemInformation;
import org.folio.edge.sip2.domain.messages.responses.ItemInformationResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.ItemRepository;
import org.folio.edge.sip2.session.SessionData;

public class ItemInformationHandler implements ISip2RequestHandler {
  private static final Logger log = LogManager.getLogger();

  private final ItemRepository itemRepository;
  private final Template commandTemplate;

  @Inject
  ItemInformationHandler(
      ItemRepository itemRepository,
        @Named("itemInformationResponse") Template commandTemplate) {
    this.itemRepository = Objects.requireNonNull(itemRepository,
        "ItemRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    final ItemInformation itemInformation = (ItemInformation) message;

    log.debug("ItemInformation: {}", () -> itemInformation);

    final Future<ItemInformationResponse> itemInformationFuture =
        itemRepository.performItemInformationCommand(itemInformation, sessionData);

    return itemInformationFuture.compose(itemInformationResponse -> {
      log.info("ItemInformationResponse: {}", () -> itemInformationResponse);

      final Map<String, Object> root = new HashMap<>();
      root.put("formatDateTime", new FormatDateTimeMethodModel());
      root.put("delimiter", sessionData.getFieldDelimiter());
      root.put("itemInformationResponse", itemInformationResponse);
      root.put("timezone", sessionData.getTimeZone());

      final String response = FreemarkerUtils
          .executeFreemarkerTemplate(root, commandTemplate);

      log.debug("SIP itemInformation response: {}", response);

      return Future.succeededFuture(response);
    });
  }
}
