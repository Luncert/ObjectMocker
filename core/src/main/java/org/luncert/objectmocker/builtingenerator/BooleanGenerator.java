package org.luncert.objectmocker.builtingenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.LambdaBasedGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class BooleanGenerator extends LambdaBasedGenerator<Boolean> {

  private static BooleanGenerator instance;

  private BooleanGenerator(ObjectSupplier<Boolean> supplier) {
    super(supplier);
  }

  /**
   * Return singleton BooleanGenerator.
   * @return BooleanGenerator
   */
  static BooleanGenerator singleton() {
    if (instance == null) {
      instance = new BooleanGenerator((ctx, clz) -> RandomUtils.nextBoolean());
    }
    return instance;
  }
}
