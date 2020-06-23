package org.luncert.objectmocker.core;

import static org.luncert.objectmocker.core.RealObjectMockContext.BUILTIN_GENERATORS;
import static org.luncert.objectmocker.core.RealObjectMockContext.DEFAULT_LIST_SIZE;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.exception.GeneratorException;
import org.luncert.objectmocker.util.ReflectionUtils;

/**
 * ObjectGenerator.
 * @author Luncert
 */
@Slf4j
public class ObjectGenerator implements Serializable, IObjectMockContextAware {

  private static final long serialVersionUID = 5287347012157068215L;
  
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
  
  protected ObjectMockContext context;
  
  // target type to generate
  @Getter
  private final Class<?> targetType;
  
  // ignoring fields
  Set<String> ignores = new HashSet<>();
  
  // specify field generators
  Map<Field, AbstractGenerator> fieldGenerators = new HashMap<>();

  ObjectGenerator(Class<?> clazz) {
    this.targetType = clazz;
  }

  ObjectGenerator(Class<?> clazz, Set<String> ignores,
                          Map<Field, AbstractGenerator> fieldGenerators) {
    this.targetType = clazz;
    this.ignores.addAll(ignores);
    this.fieldGenerators.putAll(fieldGenerators);
  }

  /**
   * If this method is invoked, this ObjectGenerator must have completed building.
   * User shouldn't invoke this method, all actions will be carried on with ObjectMockerContext.
   * @param context ObjectMockContext
   */
  @Override
  public void setObjectMockContext(ObjectMockContext context) {
    this.context = context;
    fieldGenerators.values().forEach(
        generator -> generator.setObjectMockContext(context));
  }

  /**
   * Add ignores.
   * @param ignores string array of fields need be ignored.
   */
  public void addIgnores(String...ignores) {
    this.ignores.addAll(Arrays.asList(ignores));
  }
  
  public boolean hasIgnore(String ignore) {
    return ignores.contains(ignore);
  }

  /**
   * Remove ignores.
   * @param ignores string array of fields need be removed from ignores.
   */
  public void removeIgnores(String...ignores) {
    this.ignores.removeAll(Arrays.asList(ignores));
  }
  
  /**
   * Get a set copied from the original ignoring set.
   * @return copied ignoring set
   */
  Set<String> getIgnores() {
    return new HashSet<>(ignores);
  }

  /**
   * Provide a special value for target field.
   * @param fieldName field name of target class
   * @param value Lambda expression, implementation of {@link ValueSupplier}
   * @throws Exception failed to set generator
   */
  @SuppressWarnings("unchecked")
  public void setGenerator(String fieldName, ValueSupplier value) throws Exception {
    Object v = value.get();
    setGenerator(fieldName, new LambdaBasedGenerator((clx, clz) -> v));
  }

  /**
   * Provide a generator in lambda expr for specified field.
   * @param fieldName field name of target class
   * @param supplier Lambda expression, implementation of {@link ObjectSupplier}
   * @throws NoSuchFieldException throw exception if couldn't find target field
   */
  @SuppressWarnings("unchecked")
  public void setGenerator(String fieldName, ObjectSupplier supplier) throws NoSuchFieldException {
    setGenerator(fieldName, new LambdaBasedGenerator(supplier));
  }

  /**
   * Provide a customizing generator generator for specified field.
   * If there is another generator configured for specified field, it will be overwrite.
   * @param fieldName field name of target class
   * @param fieldGenerator customized generator, must implement {@link AbstractGenerator}
   * @throws NoSuchFieldException throw exception if couldn't find target field
   */
  public void setGenerator(String fieldName, AbstractGenerator fieldGenerator)
      throws NoSuchFieldException {
    Objects.requireNonNull(fieldName);
    Objects.requireNonNull(fieldGenerator);
    fieldGenerator.setObjectMockContext(this.context);
    fieldGenerators.put(ReflectionUtils.getField(targetType, fieldName),
        fieldGenerator);
  }

  /**
   * create a new instance and generate fields value for it.
   * @param tmpIgnores ignore specified fields
   * @return target object
   */
  @SuppressWarnings("unchecked")
  Object generate(String...tmpIgnores) {
    String className = targetType.getSimpleName();

    // try to create new instance for target class
    
    Object target;
    try {
      target = targetType.getConstructor().newInstance();
    } catch (Exception e) {
      throw new GeneratorException(e, "Failed to create a new instance of target class %s.",
          className);
    }

    // generate field values for new instance
    
    Set<String> tmpIgnoreSet = tmpIgnores.length == 0
        ? Collections.EMPTY_SET : new HashSet<>(Arrays.asList(tmpIgnores));

    Class<?> objectClass = this.targetType;
    // loop to scan all fields of target type, including its super classes.
    while (!Object.class.equals(objectClass)) {
      try {
        for (Field field : objectClass.getDeclaredFields()) {
          String fieldName = field.getName();

          // skip static, final or jvm-generated fields
          int modifiers = field.getModifiers();
          if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || field.isSynthetic()) {
            log.debug("{}.{} - Field has been skipped because it is static, final,"
                + " or generated by compiler.", className, fieldName);
            continue;
          }

          // skip field need be ignored
          if (!tmpIgnoreSet.contains(fieldName) && !hasIgnore(fieldName)) {
            // To set value for field, we need set it accessible at first
            field.setAccessible(true);
            field.set(target, generateField(field));
          }
        }
      } catch (IllegalAccessException e) {
        throw new GeneratorException(e,
              "Failed to set generated field value for instance of class %s.", className);
      }

      // turn to parent class
      objectClass = objectClass.getSuperclass();
    }
    
    return targetType.cast(target);
  }
  
  @SuppressWarnings("unchecked")
  protected Object generateField(Field field) {
    Class<?> fieldType = field.getType();
    // generate field value using fieldGenerator
    AbstractGenerator generator = fieldGenerators.get(field);
    if (generator != null) {
      Class<?> elemType = fieldType;
      // if field is a list, we should forward its parameter type to the generator
      if (List.class.equals(elemType)) {
        elemType = ReflectionUtils.getParameterType(field);
      }
      return generator.generate(elemType);
    } else if ((generator = BUILTIN_GENERATORS.get(fieldType)) != null) {
      // generate field value using built-in generator
      return generator.generate(fieldType);
    } else if (fieldType.isEnum()) {
      // if target field need an enum value, generate value directly
      return generateEnum(fieldType);
    } else if (List.class.equals(field.getType())) {
      // generate list field
      return generateList(field);
    } else {
      return context.generate(fieldType);
    }
  }

  private <T> T generateEnum(Class<T> type) {
    T[] enumValues = type.getEnumConstants();
    // cannot generate value with empty enum
    if (enumValues.length == 0) {
      throw new GeneratorException("Couldn't generate a value with empty enum"
          + type.getSimpleName() + ".");
    }
    return enumValues[RandomUtils.nextInt(0, enumValues.length)];
  }

  @SuppressWarnings("unchecked")
  private Object generateList(Field field) {
    ObjectSupplier supplier;
    List list = new ArrayList<>();
    Class<?> elemClass = ReflectionUtils.getParameterType(field);

    if (BUILTIN_GENERATORS.containsKey(elemClass)) {
      supplier = (context, clazz) -> BUILTIN_GENERATORS.get(elemClass).generate(clazz);
    } else if (elemClass.isEnum()) {
      Object[] enumValues = elemClass.getEnumConstants();
      supplier = (context, clazz) -> enumValues[RandomUtils.nextInt(0, enumValues.length)];
    } else {
      supplier = (context, clazz) -> context.generate(elemClass);
    }

    for (int i = 0; i < DEFAULT_LIST_SIZE; i++) {
      list.add(supplier.getObject(context, elemClass));
    }

    return list;
  }

  /**
   * Get ObjectGeneratorBuilder.
   * @param targetType target class
   * @return ObjectGeneratorBuilder
   */
  public static ObjectGeneratorBuilder builder(Class<?> targetType) {
    return new ObjectGeneratorBuilder(targetType);
  }

  /**
   * Fast build ObjectGenerator with user provided configuration.
   */
  public static class ObjectGeneratorBuilder {
    private ObjectGenerator ins;

    private ObjectGeneratorBuilder(Class<?> clazz) {
      ins = new ObjectGenerator(clazz);
    }

    public ObjectGeneratorBuilder addIgnores(String...ignores) {
      ins.ignores.addAll(Arrays.asList(ignores));
      return this;
    }

    /**
     * Provide a special value for target field.
     * @param fieldName field name of target class
     * @param value Lambda expression, implementation of {@link ValueSupplier}
     * @return ObjectGeneratorBuilder
     * @throws Exception failed to set generator
     */
    @SuppressWarnings("unchecked")
    public ObjectGeneratorBuilder field(String fieldName, ValueSupplier value)
        throws Exception {
      Object v = value.get();
      return field(fieldName, new LambdaBasedGenerator((clx, clz) -> v));
    }

    /**
     * Provide a function-interface generator for specified field.
     * @param fieldName field name of target class
     * @param supplier Lambda expression, implementation of {@link ObjectSupplier}
     * @return ObjectGeneratorBuilder
     * @throws NoSuchFieldException throw exception if couldn't find target field
     */
    @SuppressWarnings("unchecked")
    public ObjectGeneratorBuilder field(String fieldName, ObjectSupplier supplier)
        throws NoSuchFieldException {
      return field(fieldName, new LambdaBasedGenerator(supplier));
    }

    /**
     * Provide a generator for specified field.
     * Only available when builds ObjectGenerator.
     * @param fieldName field name of target class
     * @param fieldGenerator customized generator, must be implementation
     *                      of {@link AbstractGenerator}
     * @throws NoSuchFieldException throw exception if couldn't find target field
     */
    public ObjectGeneratorBuilder field(String fieldName, AbstractGenerator fieldGenerator)
        throws NoSuchFieldException {
      Objects.requireNonNull(fieldName);
      Objects.requireNonNull(fieldGenerator);
      Field field = ReflectionUtils.getField(ins.targetType, fieldName);
      if (ins.fieldGenerators.containsKey(field)) {
        throw new InvalidParameterException("One generator has been set for target field: "
            + fieldName + ", you could only set a field generator for each"
            + " field once when build ObjectGenerator.");
      }
      ins.fieldGenerators.put(field, fieldGenerator);
      return this;
    }

    /**
     * Extend a basic ObjectGenerator, including ObjectMockContext,
     * ignores and part of field generators.
     * @param basicGenerator basic ObjectGenerator
     * @return ObjectGenerator
     */
    public ObjectGenerator extend(final ObjectGenerator basicGenerator) {
      if (!ins.targetType.equals(basicGenerator.targetType)) {
        throw new GeneratorException("Extended ObjectGenerator should"
            + " has the same target class as the basic ObjectGenerator.");
      }
      ins.context = basicGenerator.context;
      ins.ignores.addAll(basicGenerator.ignores);
      for (Map.Entry<Field, AbstractGenerator> entry : basicGenerator.fieldGenerators.entrySet()) {
        if (!ins.fieldGenerators.containsKey(entry.getKey())) {
          ins.fieldGenerators.put(entry.getKey(), entry.getValue());
        }
      }
      return ins;
    }

    public ObjectGenerator build() {
      return ins;
    }
  }
}
