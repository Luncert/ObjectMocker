package org.luncert.objectmocker.builtingenerator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class StringGenerator extends AbstractGenerator<String> {

  private StringGenerator(ObjectSupplier<String> supplier) {
    super(supplier);
  }

  static StringGenerator withLength(int len) {
    if (len <= 0) {
      throw new IllegalArgumentException("len must be positive");
    }
    return new StringGenerator(
        (ctx, clz) -> RandomStringUtils.randomAlphabetic(len));
  }

  static StringGenerator rangeFrom(String...rangeValue) {
    if (rangeValue.length == 0) {
      throw new IllegalArgumentException("rangeValue must be non-empty array");
    } else if (rangeValue.length == 1) {
      return new StringGenerator((ctx, clz) -> rangeValue[0]);
    } else {
      return new StringGenerator(
          (ctx, clz) -> rangeValue[RandomUtils.nextInt(0, rangeValue.length)]);
    }
  }
}
