package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

public class LongGenerator extends AbstractGenerator<Long> {

  private LongGenerator(ObjectSupplier<Long> supplier) {
    super(supplier);
  }

  public static LongGenerator defaultValue(Long defaultValue) {
    return new LongGenerator(
        (ctx, clz) -> defaultValue);
  }

  public static LongGenerator rangeFrom(Long start, Long end) {
    return new LongGenerator(
        (ctx, clz) -> RandomUtils.nextLong(start, end));
  }
}
