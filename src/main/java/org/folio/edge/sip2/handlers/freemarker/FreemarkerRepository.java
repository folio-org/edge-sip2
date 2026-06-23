package org.folio.edge.sip2.handlers.freemarker;

import static java.util.Locale.ROOT;
import static org.folio.edge.sip2.parser.Command.ACS_STATUS;
import static org.folio.edge.sip2.parser.Command.CHECKIN_RESPONSE;
import static org.folio.edge.sip2.parser.Command.CHECKOUT_RESPONSE;
import static org.folio.edge.sip2.parser.Command.END_SESSION_RESPONSE;
import static org.folio.edge.sip2.parser.Command.FEE_PAID_RESPONSE;
import static org.folio.edge.sip2.parser.Command.ITEM_INFORMATION_RESPONSE;
import static org.folio.edge.sip2.parser.Command.LOGIN_RESPONSE;
import static org.folio.edge.sip2.parser.Command.PATRON_INFORMATION_RESPONSE;
import static org.folio.edge.sip2.parser.Command.PATRON_STATUS_RESPONSE;
import static org.folio.edge.sip2.parser.Command.RENEW_ALL_RESPONSE;
import static org.folio.edge.sip2.parser.Command.RENEW_RESPONSE;
import static org.folio.edge.sip2.parser.Command.REQUEST_SC_RESEND;
import static org.folio.edge.sip2.parser.Command.SC_STATUS;
import static org.folio.edge.sip2.utils.Utils.getEnvOrDefault;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.EnumMap;
import java.util.IllformedLocaleException;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.parser.Command;

public class FreemarkerRepository {

  private static final String SIP2_LOCALE_PROPERTY = "sip2TemplateLocale";
  private static final String SIP2_LOCALE_ENV_VAR = "SIP2_TEMPLATE_LOCALE";

  private EnumMap<Command, Template> templates;
  private final Logger log;

  /**
   * Get the template by Command.
   *
   * @param command the Command that serves as a Key to the runtime cache
   * @return Compiled freemarker template
   */
  public Template getFreemarkerTemplate(Command command) {
    return templates.get(command);
  }

  /**
   * Creates a FreemarkerRepository using the locale configured via the
   * {@code sip2Locale} system property or {@code SIP2_LOCALE} environment variable.
   * Falls back to {@link Locale#ROOT} if neither is set or the value cannot be parsed.
   */
  public FreemarkerRepository() {
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    initializeTemplates();
  }

  private void initializeTemplates() {
    templates = new EnumMap<>(Command.class);

    Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
    configuration.setClassForTemplateLoading(FreemarkerRepository.class, "/templates");
    configuration.setDefaultEncoding("UTF-8");
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    configuration.setLogTemplateExceptions(false);
    configuration.setWrapUncheckedExceptions(true);

    configuration.setLocale(getEnvOrDefault(
        SIP2_LOCALE_PROPERTY, SIP2_LOCALE_ENV_VAR, ROOT, this::parseLocale));

    addTemplate(CHECKOUT_RESPONSE, "CheckoutResponse.ftl", configuration);
    addTemplate(CHECKIN_RESPONSE, "CheckinResponse.ftl", configuration);
    addTemplate(ACS_STATUS, "acs-status.ftl", configuration);
    addTemplate(REQUEST_SC_RESEND, "RequestSCResend.ftl", configuration);
    addTemplate(LOGIN_RESPONSE, "LoginResponse.ftl", configuration);
    addTemplate(PATRON_INFORMATION_RESPONSE, "PatronInformationResponse.ftl", configuration);
    addTemplate(PATRON_STATUS_RESPONSE, "PatronStatusResponse.ftl", configuration);
    addTemplate(END_SESSION_RESPONSE, "EndSessionResponse.ftl", configuration);
    addTemplate(ITEM_INFORMATION_RESPONSE, "ItemInformationResponse.ftl", configuration);
    addTemplate(RENEW_RESPONSE, "RenewResponse.ftl", configuration);
    addTemplate(RENEW_ALL_RESPONSE, "RenewAllResponse.ftl", configuration);
    addTemplate(FEE_PAID_RESPONSE, "FeePaidResponse.ftl", configuration);
    addTemplate(SC_STATUS, "acs-status.ftl", configuration);

  }

  private Locale parseLocale(String tag) {
    try {
      var locale = new Locale.Builder().setLanguageTag(tag).build();
      log.info("parseLocale:: Freemarker locale: {}", locale.toString());
      return locale;
    } catch (IllformedLocaleException e) {
      log.warn("Invalid locale tag '{}', falling back to locale: root", tag);
      return ROOT;
    }
  }

  private void addTemplate(Command command, String templateName, Configuration configuration) {

    Template template;

    try {
      template = configuration.getTemplate(templateName);
      template.setBooleanFormat("Y,N");
      templates.put(command, template);
    } catch (IOException e) {
      log.error("Error loading template: {}", templateName);
    }
  }
}
