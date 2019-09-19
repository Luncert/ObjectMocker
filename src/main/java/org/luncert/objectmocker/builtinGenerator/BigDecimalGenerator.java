package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

import java.math.BigDecimal;

public class BigDecimalGenerator extends AbstractGenerator<BigDecimal> {

  private BigDecimalGenerator(ObjectSupplier<BigDecimal> supplier) {
    super(supplier);
  }

  public static BigDecimalGenerator defaultValue(Double defaultValue) {
    return new BigDecimalGenerator(
        (ctx, clz) -> BigDecimal.valueOf(defaultValue));
  }

  public static BigDecimalGenerator rangeFrom(Double start, Double end) {
    return new BigDecimalGenerator(
        (ctx, clz) -> BigDecimal.valueOf(RandomUtils.nextDouble(start, end)));
  }
}

