package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

public class IntegerGenerator extends AbstractGenerator<Integer> {

  private IntegerGenerator(ObjectSupplier<Integer> supplier) {
    super(supplier);
  }

  public static IntegerGenerator defaultValue(int defaultValue) {
    return new IntegerGenerator((ctx, clz) -> defaultValue);
  }

  public static IntegerGenerator rangeFrom(int start, int end) {
    return new IntegerGenerator(
        (ctx, clz) -> RandomUtils.nextInt(start, end));
  }
}
