package org.luncert.objectmocker.builtingenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class DoubleGenerator extends AbstractGenerator<Double> {

  private DoubleGenerator(ObjectSupplier<Double> supplier) {
    super(supplier);
  }

  static DoubleGenerator defaultValue(Double defaultValue) {
    return new DoubleGenerator((ctx, clz) -> defaultValue);
  }

  static DoubleGenerator rangeFrom(Double start, Double end) {
    return new DoubleGenerator(
        (ctx, clz) -> RandomUtils.nextDouble(start, end));
  }
}