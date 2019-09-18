package org.luncert.objectmocker.builtinGenerator;

import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.exception.GeneratorException;

import java.time.ZonedDateTime;

public class ZonedDateTimeGenerator extends AbstractGenerator<ZonedDateTime> {

  @Override
  public ZonedDateTime generate(Class<?> clazz) throws GeneratorException {
    return ZonedDateTime.now();
  }
}
