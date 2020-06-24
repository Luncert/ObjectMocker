package org.luncert.objectmocker.builtingenerator;

import java.util.ArrayList;
import java.util.List;

import org.luncert.objectmocker.core.DynamicTypeGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

class ListGenerator extends DynamicTypeGenerator<List> {

  private ListGenerator(ObjectSupplier<List> supplier) {
    super(supplier);
  }

  static ListGenerator withLength(int len) {
    checkLength(len);
    return new ListGenerator((context, clazz) -> {
      List<Object> list = new ArrayList<>(len);
      for (int i = 0; i < len; i++) {
        list.add(context.generate(clazz));
      }
      return list;
    });
  }

  static ListGenerator withElementGenerator(int len, ObjectSupplier<Object> elementGenerator) {
    checkLength(len);
    return new ListGenerator((ctx, cls) -> {
      List<Object> list = new ArrayList<>(len);
      for (int i = 0; i < len; i++) {
        list.add(elementGenerator.getObject(ctx, cls));
      }
      return list;
    });
  }

  private static void checkLength(int len) {
    if (len <= 0) {
      throw new IllegalArgumentException("len must be positive");
    }
  }
}
