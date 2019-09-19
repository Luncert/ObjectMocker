package org.luncert.objectmocker.builtinGenerator;

import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

import java.util.Date;

public class DateGenerator extends AbstractGenerator<Date> {

  private static DateGenerator instance;

  private DateGenerator(ObjectSupplier<Date> supplier) {
    super(supplier);
  }

  public static DateGenerator singleton() {
    if (instance == null) {
      instance = new DateGenerator((ctx, clz) -> new Date());
    }
    return instance;
  }
}
