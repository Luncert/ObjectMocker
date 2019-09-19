package org.luncert.objectmocker.core;

import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.bigDecimalGenerator;
import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.booleanGenerator;
import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.dateGenerator;
import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.doubleGenerator;
import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.integerGenerator;
import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.longGenerator;
import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.stringGenerator;
import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.uuidGenerator;
import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.zonedDateTimeGenerator;

import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.luncert.objectmocker.exception.GeneratorException;

/**
 * @author Luncert
 */
public class ObjectMockContext {

  // Field AbstractGenerator Default Config

  private static final int DEFAULT_STRING_LENGTH = 8;
  static final int DEFAULT_LIST_SIZE = 8;

  /**
   * default Integer range.
   */
  private static final int INTEGER_START = 0;
  private static final int INTEGER_END = Integer.MAX_VALUE;

  /**
   * default Long range.
   */
  private static final long LONG_START = 0L;
  private static final long LONG_END = Long.MAX_VALUE;

  /**
   * default Double range.
   */
  private static final double DOUBLE_START = 0d;
  private static final double DOUBLE_END = Double.MAX_VALUE;

  static final Map<Class, AbstractGenerator> BUILTIN_GENERATORS;

  static {
    AbstractGenerator integerGenerator = integerGenerator(INTEGER_START, INTEGER_END);
    AbstractGenerator longGenerator = longGenerator(LONG_START, LONG_END);
    AbstractGenerator doubleGenerator = doubleGenerator(DOUBLE_START, DOUBLE_END);
    BUILTIN_GENERATORS = ImmutableMap
          .<Class, AbstractGenerator>builder()
          .put(BigDecimal.class, bigDecimalGenerator(DOUBLE_START, DOUBLE_END))
          .put(Boolean.class, booleanGenerator())
          .put(Date.class, dateGenerator())
          .put(Double.class, doubleGenerator)
          .put(double.class, doubleGenerator)
          .put(Integer.class, integerGenerator)
          .put(int.class, integerGenerator)
          .put(Long.class, longGenerator)
          .put(long.class, longGenerator)
          .put(String.class, stringGenerator(DEFAULT_STRING_LENGTH))
          .put(UUID.class, uuidGenerator())
          .put(ZonedDateTime.class, zonedDateTimeGenerator())
          .put(boolean.class, booleanGenerator())
          .build();
  }

  private final Map<Class, ObjectGenerator> generators = new HashMap<>();

  /**
   * Register ObjectGenerator.
   * @param objectGenerator build with
   * {@link org.luncert.objectmocker.core.ObjectGenerator.ObjectGeneratorBuilder}
   */
  public void register(ObjectGenerator objectGenerator) {
    Objects.requireNonNull(objectGenerator);
    Class<?> targetClazz = objectGenerator.getTargetClass();
    if (generators.containsKey(targetClazz)) {
      throw new GeneratorException("One ObjectGenerator has been registered for target class: "
          + targetClazz.getName());
    }
    objectGenerator.setObjectMockContext(this);
    generators.put(targetClazz, objectGenerator);
  }

  /**
   * Only used to generate customized class (not enum, not interface).
   * @param clazz target object type
   * @param tmpIgnores ignore specified fields
   * @return generated object
   */
  public <T> T generate(Class<T> clazz, String... tmpIgnores) {
    Object target;
    ObjectGenerator generator = generators.get(clazz);
    if (generator != null) {
      target = generator.generate(tmpIgnores);
    } else {
      AbstractGenerator<?> builtinGenerator = BUILTIN_GENERATORS.get(clazz);
      if (builtinGenerator != null) {
        target = builtinGenerator.generate(clazz);
      } else {
        throw new GeneratorException("No generator registered for class %s.",
            clazz.getSimpleName());
      }
    }
    return clazz.cast(target);
  }

  /**
   * Only used to generate customized class (not enum, not interface).
   * @param clazz target object type, will be used to find the basic ObjectGenerator
   * @param extender to provide the extended new ObjectGenerator
   * @param tmpIgnores ignore specified fields
   * @return generated object
   */
  public <T> T generate(Class<T> clazz, ObjectGeneratorExtender extender,
                        String...tmpIgnores) {
    ObjectGenerator generator = generators.get(clazz);
    if (generator == null) {
      throw new GeneratorException("No basic generator registered for class "
          + clazz.getSimpleName());
    }
    try {
      generator = extender.extendObjectGenerator(generator);
    } catch (Exception e) {
      throw new GeneratorException(e);
    }
    return clazz.cast(generator.generate(tmpIgnores));
  }

  /**
   * Generate enum, list, string and other basic type with user provided generator.
   * @param supplier lambda implementation of ObjectSupplier
   * @return generated object
   */
  public <T> T generate(ObjectSupplier<T> supplier) {
    return generate(null, new AbstractGenerator<T>(supplier) {});
  }

  /**
   * Generate enum, list, string and other basic type with responsive generator.
   * e.g. {@code context.generate(ListGenerator.withLength(String.class, 10))}
   * @param clazz mandatory if generator has DynamicTypeGenerator annotation,
   *             otherwise it is optional
   * @param generator any implementation of {@link AbstractGenerator}
   * @return generated object
   */
  public <T> T generate(Class<?> clazz, AbstractGenerator<T> generator) {
    if (clazz == null && generator.isDynamicTypeGenerator()) {
      throw new GeneratorException("Parameter clazz is mandatory"
          + " when generator has @DynamicTypeGenerator annotation.");
    }
    generator.setObjectMockContext(this);
    return generator.generate(clazz);
  }

  /**
   * Get target class' ObjectGenerator to change generating strategy.
   * @param clazz target class
   * @param modifier ObjectGeneratorModifier
   */
  public void modifyObjectGenerator(Class<?> clazz, ObjectGeneratorModifier modifier)
      throws Exception {
    modifier.accept(generators.get(clazz));
  }

  /**
   * Create a new instance from this context.
   * All generator relating information will be copy into new instance.
   * @return ObjectMockContext new instance.
   */
  public ObjectMockContext copy() {
    ObjectMockContext ctx = new ObjectMockContext();
    for (Map.Entry<Class, ObjectGenerator> entry : this.generators.entrySet()) {
      ObjectGenerator generator = entry.getValue().copy();
      generator.setObjectMockContext(ctx);
      ctx.generators.put(generator.getTargetClass(), generator);
    }
    return ctx;
  }
}
