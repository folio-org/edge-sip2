package org.folio.edge.sip2.handlers.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FreemarkerUtils {

  private FreemarkerUtils() {}

  private static final Logger log;

  static {
    log = LogManager.getLogger();
  }

  /**
   * Utility method that executes a freemarker template to return a templated-formated string.
   *
   * @param data the data used by the template.
   * @param template the template to apply to the data.
   * @return String output of running the template on the data
   */
  public static String executeFreemarkerTemplate(Object data, Template template) {

    Writer out = new StringWriter();
    String outputString = "";

    try {
      template.process(data, out);
      outputString = out.toString();
    } catch (TemplateException e) {
      log.error("Having problems finding and loading template: " + e.getMessage());
    } catch (IOException ioEx) {
      log.error("Having problems applying template to data: " + ioEx.getMessage());
    } catch (Exception ex) {
      log.error("Error applying template to data: " + ex.getMessage());
    }

    log.info("Data = {} Template = {}",
        () -> data == null ? "" : data.toString(),
        template::getName);

    return outputString;
  }
}
