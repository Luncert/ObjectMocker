package org.luncert.objectmocker.builtinGenerator;

import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

import java.util.UUID;

public class UuidGenerator extends AbstractGenerator<UUID> {

  private static UuidGenerator instance;

  private UuidGenerator(ObjectSupplier<UUID> supplier) {
    super(supplier);
  }

  public static UuidGenerator singleton() {
    if (instance == null) {
      instance = new UuidGenerator((ctx, clz) -> UUID.randomUUID());
    }
    return instance;
  }
}
