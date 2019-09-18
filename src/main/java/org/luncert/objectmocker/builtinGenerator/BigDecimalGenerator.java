package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.exception.GeneratorException;

import java.math.BigDecimal;

public class BigDecimalGenerator extends AbstractGenerator<BigDecimal> {

  @Override
  public BigDecimal generate(Class<?> clazz) throws GeneratorException {
    return BigDecimal.valueOf(RandomUtils.nextDouble());
  }
}

