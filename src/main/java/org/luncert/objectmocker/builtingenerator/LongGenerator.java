package org.luncert.objectmocker.builtingenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.LambdaBasedGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class LongGenerator extends LambdaBasedGenerator<Long> {

  private LongGenerator(ObjectSupplier<Long> supplier) {
    super(supplier);
  }

  static LongGenerator defaultValue(Long defaultValue) {
    return new LongGenerator(
        (ctx, clz) -> defaultValue);
  }

  static LongGenerator rangeFrom(Long start, Long end) {
    if (start >= end) {
      throw new IllegalArgumentException("end must be bigger than start");
    }
    boolean overflow = end - start < 0;
    final long e1 = overflow ? Long.MAX_VALUE : end - start;
    final long e2 = overflow ? -(start + 1) - (Long.MAX_VALUE - end) : 0;
    return new LongGenerator(
        (ctx, clz) -> (RandomUtils.nextLong(0, e1) + start + RandomUtils.nextLong(0, e2)));
  }
}
