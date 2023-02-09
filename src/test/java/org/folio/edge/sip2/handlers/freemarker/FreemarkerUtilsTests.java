package org.folio.edge.sip2.handlers.freemarker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import freemarker.template.Template;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.folio.edge.sip2.parser.Command;
import org.junit.jupiter.api.Test;


public class FreemarkerUtilsTests {
  @Test
  public void cannotExecuteFreemarkerTemplateGivenNullData() {
    Template acsStatusTemplate = FreemarkerRepository.getInstance()
                                    .getFreemarkerTemplate(Command.ACS_STATUS);
    String result = FreemarkerUtils.executeFreemarkerTemplate(null, acsStatusTemplate);
    assertEquals("", result);
  }

  @Test
  public void cannotExecuteFreemarkerTemplateGivenInvalidData() {
    Object data = new Date(); //give it some bogus class
    Template acsStatusTemplate = FreemarkerRepository.getInstance()
                                    .getFreemarkerTemplate(Command.ACS_STATUS);
    String result = FreemarkerUtils.executeFreemarkerTemplate(data, acsStatusTemplate);
    assertEquals("", result);
  }

  @Test
  public void cannotExecuteFreemarkerTemplateGivenInvalidTimeZone() {
    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", "|");
    root.put("checkoutResponse", null);
    root.put("timezone", null);

    Template acsStatusTemplate = FreemarkerRepository.getInstance()
        .getFreemarkerTemplate(Command.ACS_STATUS);

    final String response = FreemarkerUtils.executeFreemarkerTemplate(root, acsStatusTemplate);

    assertEquals("", response);
  }
}
