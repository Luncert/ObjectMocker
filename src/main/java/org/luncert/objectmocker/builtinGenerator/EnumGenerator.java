package org.luncert.objectmocker.builtinGenerator;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.annotation.DynamicTypeGenerator;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;
import org.luncert.objectmocker.exception.GeneratorException;

@DynamicTypeGenerator
public class EnumGenerator extends AbstractGenerator<Object> {

  private EnumGenerator(ObjectSupplier<Object> supplier) {
    super(supplier);
  }

  public static <T> EnumGenerator defaultValue(T defaultValue) {
    checkObjectType(defaultValue.getClass());
    return new EnumGenerator((ctx, clz) -> defaultValue);
  }

  public static <T> EnumGenerator rangeFrom(T[] rangeValue) {
    if (rangeValue.length == 0) {
      throw new GeneratorException("Parameter rangeValue must be a non-empty array.");
    }
    checkObjectType(rangeValue[0].getClass());

    return new EnumGenerator((ctx, clz) ->
        rangeValue[RandomUtils.nextInt(0, rangeValue.length)]);
  }

  private static void checkObjectType(Class clazz) {
    if (!clazz.isEnum()) {
      throw new GeneratorException("Class " + clazz.getSimpleName() + " is not a enum.");
    }
  }
}
