package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomStringUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.exception.GeneratorException;

public class StringGenerator extends AbstractGenerator<String> {

  @Override
  public String generate(Class<?> clazz) throws GeneratorException {
    return RandomStringUtils.randomAlphabetic(8);
  }
}
