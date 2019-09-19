package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

public class DoubleGenerator extends AbstractGenerator<Double> {

  private DoubleGenerator(ObjectSupplier<Double> supplier) {
    super(supplier);
  }

  public static DoubleGenerator defaultValue(Double defaultValue) {
    return new DoubleGenerator((ctx, clz) -> defaultValue);
  }

  public static DoubleGenerator rangeFrom(Double start, Double end) {
    return new DoubleGenerator(
        (ctx, clz) -> RandomUtils.nextDouble(start, end));
  }
}
