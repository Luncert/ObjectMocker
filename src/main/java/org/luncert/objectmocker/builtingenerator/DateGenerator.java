package org.luncert.objectmocker.builtingenerator;

import java.util.Date;

import org.luncert.objectmocker.core.LambdaBasedGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class DateGenerator extends LambdaBasedGenerator<Date> {

  private static DateGenerator instance;

  private DateGenerator(ObjectSupplier<Date> supplier) {
    super(supplier);
  }

  static DateGenerator singleton() {
    if (instance == null) {
      instance = new DateGenerator((ctx, clz) -> new Date());
    }
    return instance;
  }
}
