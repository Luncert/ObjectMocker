package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.exception.GeneratorException;

public class DoubleGenerator extends AbstractGenerator<Double> {

  @Override
  public Double generate(Class<?> clazz) throws GeneratorException {
    return RandomUtils.nextDouble();
  }
}
