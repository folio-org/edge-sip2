package org.folio.edge.sip2.handlers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.template.Template;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultResourceProvider;
import org.junit.jupiter.api.Test;

public class HandlersFactoryTests {
  @Test
  public void canGetAcsStatusHandlerWithNullArguments() {
    ISip2RequestHandler acsStatusHandler = HandlersFactory
        .getScStatusHandlerInstance(null, null, null);
    assertNotNull(acsStatusHandler);
    assertTrue(acsStatusHandler instanceof SCStatusHandler);
  }

  @Test
  public void canGetAcsStatusHandlerWithNonNlllArguments() {

    DefaultResourceProvider resourceProvider = new DefaultResourceProvider();
    ConfigurationRepository configRepo = new ConfigurationRepository(resourceProvider);
    Template freemarkerTemplate = FreemarkerRepository.getInstance()
        .getFreemarkerTemplate(Command.ACS_STATUS);

    ISip2RequestHandler acsStatusHandler = HandlersFactory
        .getScStatusHandlerInstance(configRepo, resourceProvider,
            freemarkerTemplate);
    assertNotNull(acsStatusHandler);
    assertTrue(acsStatusHandler instanceof SCStatusHandler);
  }

  @Test
  public void canGetCheckoutHandler() {
    ISip2RequestHandler checkoutHandler = HandlersFactory.getCheckoutHandlerIntance();
    assertNotNull(checkoutHandler);
    assertTrue(checkoutHandler instanceof CheckoutHandler);
  }

  @Test
  public void canGetAcsResendHandler() {
    ISip2RequestHandler acsResendHandler = HandlersFactory.getACSResendHandler();
    assertNotNull(acsResendHandler);
    assertTrue(acsResendHandler instanceof ACSResendHandler);
  }
}
