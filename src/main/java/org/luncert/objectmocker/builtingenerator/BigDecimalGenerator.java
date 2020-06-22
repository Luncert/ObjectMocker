package org.luncert.objectmocker.builtingenerator;

import java.math.BigDecimal;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.LambdaBasedGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

/**
 * Generator for {@link java.math.BigDecimal}.
 * @author Luncert
 */
class BigDecimalGenerator extends LambdaBasedGenerator<BigDecimal> {

  private BigDecimalGenerator(ObjectSupplier<BigDecimal> supplier) {
    super(supplier);
  }

  static BigDecimalGenerator defaultValue(Double defaultValue) {
    return new BigDecimalGenerator(
        (ctx, clz) -> BigDecimal.valueOf(defaultValue));
  }

  static BigDecimalGenerator rangeFrom(Double start, Double end) {
    return new BigDecimalGenerator(
        (ctx, clz) -> BigDecimal.valueOf(RandomUtils.nextDouble(start, end)));
  }
}

