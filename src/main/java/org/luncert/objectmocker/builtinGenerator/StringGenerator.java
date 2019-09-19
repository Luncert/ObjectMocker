package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

public class StringGenerator extends AbstractGenerator<String> {

  private StringGenerator(ObjectSupplier<String> supplier) {
    super(supplier);
  }

  public static StringGenerator rangeFrom(String...rangeValue) {
    if (rangeValue.length == 0) {
      throw new IllegalArgumentException("rangeValue must be non-empty array");
    } else if (rangeValue.length == 1){
      return new StringGenerator((ctx, clz) -> rangeValue[0]);
    } else {
      return new StringGenerator(
          (ctx, clz) -> rangeValue[RandomUtils.nextInt(0, rangeValue.length)]);
    }
  }

  public static StringGenerator withLength(int len) {
    return new StringGenerator(
        (ctx, clz) -> RandomStringUtils.randomAlphabetic(len));
  }
}
