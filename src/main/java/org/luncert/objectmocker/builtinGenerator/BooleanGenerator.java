package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

public class BooleanGenerator extends AbstractGenerator<Boolean> {

  private static BooleanGenerator instance;

  private BooleanGenerator(ObjectSupplier<Boolean> supplier) {
    super(supplier);
  }

  public static BooleanGenerator singleton() {
    if (instance == null) {
      instance = new BooleanGenerator((ctx, clz) -> RandomUtils.nextBoolean());
    }
    return instance;
  }
}
