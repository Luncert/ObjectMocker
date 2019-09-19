package org.luncert.objectmocker.builtingenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class BooleanGenerator extends AbstractGenerator<Boolean> {

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
