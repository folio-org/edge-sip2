package org.folio.edge.sip2.handlers;

import freemarker.template.Template;

import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultResourceProvider;
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
      IResourceProvider<Object> resProvider,
      Template freeMarkerTemplate) {
    resProvider = getResourceProvider(resProvider);

    if (configRepo == null) {
      configRepo = new ConfigurationRepository(resProvider);
    }

    freeMarkerTemplate = getCommandTemplate(freeMarkerTemplate,
        Command.ACS_STATUS);

    return new SCStatusHandler(configRepo, freeMarkerTemplate);
  }

  public static ISip2RequestHandler getCheckoutHandlerIntance() {
    return new CheckoutHandler();
  }

  public static ISip2RequestHandler getInvalidMessageHandler() {
    return new InvalidMessageHandler();
  }

  @SuppressWarnings("unchecked")
  private static <T> IResourceProvider<T> getResourceProvider(
      IResourceProvider<T> resourceProvider) {
    if (resourceProvider == null) {
      return (IResourceProvider<T>) new DefaultResourceProvider();
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
