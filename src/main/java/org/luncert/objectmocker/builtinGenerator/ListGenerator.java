package org.luncert.objectmocker.builtinGenerator;

import org.luncert.objectmocker.annotation.DynamicTypeGenerator;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

import java.util.ArrayList;
import java.util.List;

@DynamicTypeGenerator
public class ListGenerator extends AbstractGenerator<List> {

  private ListGenerator(ObjectSupplier<List> supplier) {
    super(supplier);
  }

  public static ListGenerator withLength(int len) {
    return new ListGenerator((context, clazz) -> {
      List<Object> list = new ArrayList<>();
      for (int i = 0; i < len; i++) {
        list.add(context.generate(clazz));
      }
      return list;
    });
  }

  public static ListGenerator withElementGenerator(int len, ObjectSupplier<Object> elementGenerator) {
    return new ListGenerator((ctx, cls) -> {
      List<Object> list = new ArrayList<>();
      for (int i = 0; i < len; i++) {
        list.add(elementGenerator.getObject(ctx, cls));
      }
      return list;
    });
  }
}
