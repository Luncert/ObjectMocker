package org.luncert.objectmocker.core;

import org.luncert.objectmocker.exception.GeneratorException;
import org.luncert.objectmocker.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Fast build ObjectGenerator with user provided configuration.
 */
public final class ObjectGeneratorBuilder<T> {
  
  public static <T> ObjectGeneratorBuilder<T> of(Class<T> targetType) {
    return new ObjectGeneratorBuilder<>(targetType);
  }
  
  public static <T> AbstractObjectGenerator<T> noFieldScanningGenerator(Class<T> targetType, NoFieldScanningObjectSupplier<T> supplier) {
    return new NoFieldScanningObjectGenerator<>(targetType, supplier);
  }
  
  private ObjectGenerator<T> ins;
  
  private ObjectGeneratorBuilder(Class<T> clazz) {
    ins = new ObjectGenerator<>(clazz);
  }
  
  public ObjectGeneratorBuilder<T> addIgnores(String...ignores) {
    ins.ignores.addAll(Arrays.asList(ignores));
    return this;
  }
  
  /**
   * If false, then generator will ignore all fields the target type extended from its super classes
   * @param scanSuperClasses boolean
   */
  public ObjectGeneratorBuilder<T> scanSuperClasses(boolean scanSuperClasses) {
    ins.setScanSuperClasses(scanSuperClasses);
    return this;
  }
  
  /**
   * If true, target type's super classes must have a relevant generator registered to mock context
   * @param useConfiguredGeneratorForSuperClasses boolean
   */
  public ObjectGeneratorBuilder<T> useConfiguredGeneratorForSuperClasses(boolean useConfiguredGeneratorForSuperClasses) {
    ins.setUseConfiguredGeneratorForSuperClasses(useConfiguredGeneratorForSuperClasses);
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
  public ObjectGeneratorBuilder<T> field(String fieldName, ValueSupplier value)
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
  public ObjectGeneratorBuilder<T> field(String fieldName, ObjectSupplier supplier)
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
  public ObjectGeneratorBuilder<T> field(String fieldName, AbstractGenerator fieldGenerator)
      throws NoSuchFieldException {
    Objects.requireNonNull(fieldName);
    Objects.requireNonNull(fieldGenerator);
    Field field = ReflectionUtils.getField(ins.getTargetType(), fieldName);
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
  public ObjectGenerator<T> extend(final ObjectGenerator<T> basicGenerator) {
    if (!ins.getTargetType().equals(basicGenerator.getTargetType())) {
      throw new GeneratorException("Extended ObjectGenerator must"
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
  
  public ObjectGenerator<T> build() {
    return ins;
  }
}
