package org.luncert.objectmocker.builtingenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.LambdaBasedGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class DoubleGenerator extends LambdaBasedGenerator<Double> {

  private DoubleGenerator(ObjectSupplier<Double> supplier) {
    super(supplier);
  }

  static DoubleGenerator defaultValue(Double defaultValue) {
    return new DoubleGenerator((ctx, clz) -> defaultValue);
  }

  static DoubleGenerator rangeFrom(Double start, Double end) {
    if (start >= end) {
      throw new IllegalArgumentException("end must be bigger than start");
    }
    boolean overflow = end - start < 0;
    final double e1 = overflow ? Integer.MAX_VALUE : end - start;
    // -Integer.MIN_VALUE is equals to Integer.MIN_VALUE,
    // so I add 1 to e2 in case of e2 = Integer.MIN_VALUE
    final double e2 = overflow ? -(start + 1) - (Integer.MAX_VALUE - end) : 0;
    return new DoubleGenerator(
        (ctx, clz) -> (RandomUtils.nextDouble(0, e1) + start + RandomUtils.nextDouble(0, e2)));
  }
}
