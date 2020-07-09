package org.folio.edge.sip2.handlers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.template.Template;
import io.vertx.core.Vertx;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultResourceProvider;
import org.folio.edge.sip2.repositories.IRequestData;
import org.folio.edge.sip2.repositories.IResourceProvider;
import org.junit.jupiter.api.Test;

public class HandlersFactoryTests {
  @Test
  public void canGetAcsStatusHandlerWithNullArguments() {
    ISip2RequestHandler acsStatusHandler = HandlersFactory
        .getScStatusHandlerInstance(null, null, null, null, "http://abcdefg.com", Vertx.vertx());
    assertNotNull(acsStatusHandler);
    assertTrue(acsStatusHandler instanceof SCStatusHandler);
  }

  @Test
  public void canGetAcsStatusHandlerWithNonNullArguments() {
    IResourceProvider<IRequestData> resourceProvider =
        new DefaultResourceProvider("json/DefaultACSConfiguration.json");
    ConfigurationRepository configRepo = new ConfigurationRepository(resourceProvider,
        TestUtils.getUtcFixedClock());
    Template freemarkerTemplate = FreemarkerRepository.getInstance()
        .getFreemarkerTemplate(Command.ACS_STATUS);

    ISip2RequestHandler acsStatusHandler = HandlersFactory
        .getScStatusHandlerInstance(configRepo, resourceProvider,
            freemarkerTemplate, null, "abcdefg", Vertx.vertx());
    assertNotNull(acsStatusHandler);
    assertTrue(acsStatusHandler instanceof SCStatusHandler);
  }

  @Test
  public void canGetAcsResendHandler() {
    ISip2RequestHandler acsResendHandler = HandlersFactory.getACSResendHandler();
    assertNotNull(acsResendHandler);
    assertTrue(acsResendHandler instanceof ACSResendHandler);
  }
}
