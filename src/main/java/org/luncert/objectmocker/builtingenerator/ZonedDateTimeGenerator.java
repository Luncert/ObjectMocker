package org.luncert.objectmocker.builtingenerator;

import java.time.ZonedDateTime;

import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class ZonedDateTimeGenerator extends AbstractGenerator<ZonedDateTime> {

  private static ZonedDateTimeGenerator instance;

  private ZonedDateTimeGenerator(ObjectSupplier<ZonedDateTime> supplier) {
    super(supplier);
  }

  static ZonedDateTimeGenerator singleton() {
    if (instance == null) {
      instance = new ZonedDateTimeGenerator((ctx, clz) -> ZonedDateTime.now());
    }
    return instance;
  }
}
