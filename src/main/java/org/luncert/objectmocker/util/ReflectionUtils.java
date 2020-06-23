package org.luncert.objectmocker.util;

import org.luncert.objectmocker.exception.GeneratorException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ReflectionUtils {
  
  private ReflectionUtils() {}
  
  public static Field getField(Class<?> targetType, String fieldName) throws NoSuchFieldException {
    Field field;
    try {
      field = targetType.getField(fieldName);
    } catch (NoSuchFieldException e) {
      field = targetType.getDeclaredField(fieldName);
    }
    return field;
  }
  
  public static Class<?> getParameterType(Field field) {
    // determine element type
    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
    Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
    if (actualTypeArgs.length == 0) {
      throw new GeneratorException("Failed to determine parameterized type of list type field "
          + field.getName() + " for class "
          + field.getDeclaringClass().getSimpleName() + ".");
    }
    return (Class) actualTypeArgs[0];
  }
}
