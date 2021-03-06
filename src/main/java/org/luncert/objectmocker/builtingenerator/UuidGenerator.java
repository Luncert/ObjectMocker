package org.luncert.objectmocker.builtingenerator;

import java.util.UUID;

import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class UuidGenerator extends AbstractGenerator<UUID> {

  private static UuidGenerator instance;

  private UuidGenerator(ObjectSupplier<UUID> supplier) {
    super(supplier);
  }

  static UuidGenerator singleton() {
    if (instance == null) {
      instance = new UuidGenerator((ctx, clz) -> UUID.randomUUID());
    }
    return instance;
  }
}
