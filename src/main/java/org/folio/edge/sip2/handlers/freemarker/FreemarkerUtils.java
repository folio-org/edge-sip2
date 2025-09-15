package org.folio.edge.sip2.handlers.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;

public class FreemarkerUtils {

  private FreemarkerUtils() {}

  private static final Sip2LogAdapter log;

  static {
    log = Sip2LogAdapter.getLogger(FreemarkerUtils.class);
  }

  /**
   * Utility method that executes a freemarker template to return a templated-formated string.
   *
   * @param data the data used by the template.
   * @param template the template to apply to the data.
   * @return String output of running the template on the data
   */
  public static String executeFreemarkerTemplate(SessionData sd, Object data, Template template) {

    Writer out = new StringWriter();
    String outputString = "";

    try {
      template.process(data, out);
      outputString = out.toString();
    } catch (TemplateException e) {
      log.error(sd, "Having problems finding and loading template: {} ", e.getMessage());
    } catch (IOException ioEx) {
      log.error(sd, "Having problems applying template to data: {} ", ioEx.getMessage());
    } catch (Exception ex) {
      log.error(sd, "Error applying template to data: {} ", ex.getMessage());
    }

    log.debug(sd, "Data = {} Template = {}",
        () -> data == null ? "" : data.toString(),
        template::getName);

    return outputString;
  }
}
