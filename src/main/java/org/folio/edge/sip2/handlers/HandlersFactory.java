package org.folio.edge.sip2.handlers;

import freemarker.template.Template;
import jdk.internal.org.objectweb.asm.Handle;

import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultResourceProvider;

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
  public static ISip2RequestHandler getScStatusHandlerInstance(ConfigurationRepository configRepo,
                                                               DefaultResourceProvider resProvider,
                                                               Template freeMarkerTemplate) {
    if (resProvider == null) {
      resProvider = new DefaultResourceProvider();
    }

    if (configRepo == null) {
      configRepo = new ConfigurationRepository(resProvider);
    }

    if (freeMarkerTemplate == null) {
      freeMarkerTemplate = FreemarkerRepository.getInstance()
                                               .getFreemarkerTemplate(Command.ACS_STATUS);
    }

    return new SCStatusHandler(configRepo, freeMarkerTemplate);
  }

  public static ISip2RequestHandler getLoginHandlerIntance() {
    return new LoginHandler();
  }

  public static ISip2RequestHandler getCheckoutHandlerIntance() {
    return new CheckoutHandler();
  }
}
