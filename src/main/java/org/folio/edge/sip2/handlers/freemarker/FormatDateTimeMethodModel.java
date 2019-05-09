package org.folio.edge.sip2.handlers.freemarker;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import javax.swing.text.Utilities;

import org.folio.edge.sip2.utils.Utils;

public class FormatDateTimeMethodModel implements TemplateMethodModelEx {

  @Override
  public Object exec(@SuppressWarnings("rawtypes") List args)
      throws TemplateModelException {

    if (args.size() != 3) {
      throw new TemplateModelException("Wrong arguments");
    }

    OffsetDateTime time = (OffsetDateTime) ((StringModel) args.get(0)).getWrappedObject();
    OffsetDateTime convertedTime =
        Utils.convertDateTime(time,((SimpleScalar)args.get(2)).getAsString());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                                    ((SimpleScalar)args.get(1)).getAsString());

    return formatter.format(convertedTime);
  }
}
