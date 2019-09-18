package org.luncert.objectmocker.builtinGenerator;

import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.exception.GeneratorException;

import java.util.Date;

public class DateGenerator extends AbstractGenerator<Date> {

  @Override
  public Date generate(Class<?> clazz) throws GeneratorException {
    return new Date();
  }
}
