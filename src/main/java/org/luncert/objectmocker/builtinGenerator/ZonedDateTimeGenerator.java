package org.luncert.objectmocker.builtinGenerator;

import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

import java.time.ZonedDateTime;

public class ZonedDateTimeGenerator extends AbstractGenerator<ZonedDateTime> {

  private static ZonedDateTimeGenerator instance;

  private ZonedDateTimeGenerator(ObjectSupplier<ZonedDateTime> supplier) {
    super(supplier);
  }

  public static ZonedDateTimeGenerator singleton() {
    if (instance == null) {
      instance = new ZonedDateTimeGenerator((ctx, clz) -> ZonedDateTime.now());
    }
    return instance;
  }
}
