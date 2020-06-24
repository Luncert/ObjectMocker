package org.luncert.objectmocker.util;

import org.luncert.objectmocker.exception.GeneratorException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtils {
  
  // TODO: as util
  //private static final Map<Class, ValueParser> VALUE_PARSERS;
  //
  //static {
  //  VALUE_PARSERS = ImmutableMap.<Class, ValueParser>builder()
  //      .put(BigDecimal.class, BigDecimal::new)
  //      .put(Boolean.class, Boolean::valueOf)
  //      .put(boolean.class, Boolean::valueOf)
  //      //.put(Date.class, null)
  //      .put(Double.class, Double::valueOf)
  //      .put(double.class, Double::valueOf)
  //      .put(Integer.class, Integer::valueOf)
  //      .put(int.class, Integer::valueOf)
  //      .put(Long.class, Long::valueOf)
  //      .put(long.class, Long::valueOf)
  //      .put(String.class, v -> v)
  //      .put(UUID.class, UUID::fromString)
  //      //.put(ZonedDateTime.class, null)
  //      .build();
  //}
  
  private ReflectionUtils() {}
  
  /**
   * Used to get public or private fields.
   * @param targetType class
   * @param fieldName field name
   * @return field
   * @throws NoSuchFieldException nothing found in target class
   */
  public static Field getField(Class<?> targetType, String fieldName) throws NoSuchFieldException {
    Field field;
    try {
      field = targetType.getField(fieldName);
    } catch (NoSuchFieldException e) {
      field = targetType.getDeclaredField(fieldName);
    }
    return field;
  }
  
  public static List<Class> getParameterType(Field field) {
    Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType)) {
      throw new GeneratorException("target field is not parameterized type");
    }
    
    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
    Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
    if (actualTypeArgs.length == 0) {
      // unknown condition
      throw new GeneratorException("failed to determine parameterized type of parametrized type field "
          + field.getName() + " in class "
          + field.getDeclaringClass().getSimpleName());
    }
    
    List<Class> actualTypeArgList = new ArrayList<>();
    for (Type actualTypeArg : actualTypeArgs) {
      actualTypeArgList.add((Class) actualTypeArg);
    }
    return actualTypeArgList;
  }
}
