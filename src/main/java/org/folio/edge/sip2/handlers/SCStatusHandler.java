package org.folio.edge.sip2.handlers;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.MethodHandles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;
import org.folio.edge.sip2.repositories.ConfigurationRepository;

public class SCStatusHandler implements ISip2RequestHandler {

  private final ConfigurationRepository configurationRepository;
  private final Logger log;
  private final String templatePath;

  public SCStatusHandler(ConfigurationRepository configurationRepository, String templatePath) {
    this.configurationRepository = configurationRepository;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    this.templatePath = templatePath;
  }

  @Override
  public String execute(Object message)  {
    //Process input

    File fileName = new File(templatePath);
    File[] fileList = fileName.listFiles();

    for (File file: fileList) {

      System.out.println(file);
    }

    String outputSipMessage = null;

    try {
      ACSStatus acsStatus = configurationRepository.getACSStatus("fs00000010test");
      Configuration freemakerConfig = new Configuration(Configuration.VERSION_2_3_28);
      freemakerConfig.setDirectoryForTemplateLoading(new File(templatePath));
      freemakerConfig.setDefaultEncoding("UTF-8");
      freemakerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
      freemakerConfig.setLogTemplateExceptions(false);
      freemakerConfig.setWrapUncheckedExceptions(true);

      Template template = freemakerConfig.getTemplate("acs-status.ftl");
      template.setBooleanFormat("Y,N");
      Writer stringWriter = new StringWriter();
      template.process(acsStatus, stringWriter);

      stringWriter.close();
      outputSipMessage = stringWriter.toString();
    } catch (IOException ioEx) {
      log.error("Having problems finding and loading template: " + ioEx.getMessage());
    } catch (TemplateException templEx) {
      log.error("having problems transforming using the template: " + templEx.getMessage());
    }

    return outputSipMessage;
  }
}
