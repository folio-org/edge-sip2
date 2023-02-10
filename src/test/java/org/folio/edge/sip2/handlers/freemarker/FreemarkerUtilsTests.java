package org.folio.edge.sip2.handlers.freemarker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import freemarker.template.Template;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.folio.edge.sip2.parser.Command;
import org.junit.jupiter.api.Test;


class FreemarkerUtilsTests {
  @Test
  void cannotExecuteFreemarkerTemplateGivenNullData() {
    Template acsStatusTemplate = FreemarkerRepository.getInstance()
                                    .getFreemarkerTemplate(Command.ACS_STATUS);
    String result = FreemarkerUtils.executeFreemarkerTemplate(null, acsStatusTemplate);
    assertEquals("", result);
  }

  @Test
  void cannotExecuteFreemarkerTemplateGivenInvalidData() {
    Object data = new Date(); //give it some bogus class
    Template acsStatusTemplate = FreemarkerRepository.getInstance()
                                    .getFreemarkerTemplate(Command.ACS_STATUS);
    String result = FreemarkerUtils.executeFreemarkerTemplate(data, acsStatusTemplate);
    assertEquals("", result);
  }

}
