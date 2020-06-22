package org.luncert.objectmocker.builtingenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class IntegerGenerator extends AbstractGenerator<Integer> {

  private IntegerGenerator(ObjectSupplier<Integer> supplier) {
    super(supplier);
  }

  static IntegerGenerator defaultValue(int defaultValue) {
    return new IntegerGenerator((ctx, clz) -> defaultValue);
  }

  static IntegerGenerator rangeFrom(int start, int end) {
    if (start >= end) {
      throw new IllegalArgumentException("end must be bigger than start");
    }
    boolean overflow = end - start < 0;
    final int e1 = overflow ? Integer.MAX_VALUE : end - start;
    // -Integer.MIN_VALUE is equals to Integer.MIN_VALUE,
    // so I add 1 to e2 in case of e2 = Integer.MIN_VALUE
    final int e2 = overflow ? -(start + 1) - (Integer.MAX_VALUE - end) : 0;
    return new IntegerGenerator(
        (ctx, clz) -> (RandomUtils.nextInt(0, e1) + start + RandomUtils.nextInt(0, e2)));
  }
}
