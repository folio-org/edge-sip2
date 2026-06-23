package org.folio.edge.sip2.handlers.freemarker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import freemarker.template.Template;
import java.util.Date;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
class FreemarkerUtilsTests {

  private final FreemarkerRepository repository = new FreemarkerRepository();

  @Test
  void cannotExecuteFreemarkerTemplateGivenNullData() {
    Template acsStatusTemplate = repository.getFreemarkerTemplate(Command.ACS_STATUS);
    String result = FreemarkerUtils.executeFreemarkerTemplate(null, null, acsStatusTemplate);
    assertEquals("", result);
  }

  @Test
  void cannotExecuteFreemarkerTemplateGivenInvalidData() {
    Object data = new Date(); //give it some bogus class
    Template acsStatusTemplate = repository.getFreemarkerTemplate(Command.ACS_STATUS);
    String result = FreemarkerUtils.executeFreemarkerTemplate(null, data, acsStatusTemplate);
    assertEquals("", result);
  }

}
