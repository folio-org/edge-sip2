package org.folio.edge.sip2.handlers;

import freemarker.template.Template;
import io.vertx.ext.web.client.WebClient;
import java.time.Clock;
import java.util.Objects;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.FolioResourceProvider;
import org.folio.edge.sip2.repositories.IRequestData;
import org.folio.edge.sip2.repositories.IResourceProvider;

/**
 * Factory class that holds instantiation logic for all ISip2RequestHandlers.
 * This serves as a central point to get the handlers.
 */
public class HandlersFactory {

  private HandlersFactory() {}

  /**
   * Factory method that encapsulates the logic of instantiating a SCStatusHandler.
   *
   * @param configRepo Configuration repository to configuration data.
   * @param resProvider DefaultResourceProvider instance that gets the data from a config file
   * @param freeMarkerTemplate Freemarker template instance
   * @return SCStatusHandler
   */
  public static ISip2RequestHandler getScStatusHandlerInstance(
      ConfigurationRepository configRepo,
      IResourceProvider<IRequestData> resProvider,
      Template freeMarkerTemplate,
      Clock clock,
      String okapiUrl,
      WebClient webClient) {

    Objects.requireNonNull(okapiUrl, "okapiUrl is required");
    Objects.requireNonNull(webClient, "webClient is required");

    resProvider = getResourceProvider(resProvider, okapiUrl, webClient);

    if (configRepo == null && clock == null) {
      configRepo = new ConfigurationRepository(resProvider, Clock.systemUTC());
    } else if (configRepo == null) {
      configRepo = new ConfigurationRepository(resProvider, clock);
    }

    freeMarkerTemplate = getCommandTemplate(freeMarkerTemplate,
        Command.ACS_STATUS);

    return new SCStatusHandler(configRepo, freeMarkerTemplate);
  }

  public static ISip2RequestHandler getInvalidMessageHandler() {
    return new InvalidMessageHandler();
  }

  @SuppressWarnings("unchecked")
  private static <T> IResourceProvider<T> getResourceProvider(
      IResourceProvider<T> resourceProvider, String okapiUrl, WebClient webClient) {
    if (resourceProvider == null) {
      return (IResourceProvider<T>) new FolioResourceProvider(null, okapiUrl, webClient);
    }

    return resourceProvider;
  }

  private static Template getCommandTemplate(
      Template commandTemplate,
      Command command) {
    if (commandTemplate == null) {
      return FreemarkerRepository.getInstance().getFreemarkerTemplate(command);
    }

    return commandTemplate;
  }

  public static ISip2RequestHandler getACSResendHandler() {
    return new ACSResendHandler();
  }
}
