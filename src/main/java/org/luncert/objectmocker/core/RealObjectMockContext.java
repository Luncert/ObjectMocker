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
import java.util.Optional;
import java.util.UUID;

import org.luncert.objectmocker.exception.GeneratorException;

/**
 * RealObjectMockContext.
 * @author Luncert
 */
public final class RealObjectMockContext implements ObjectMockContext {

  // default Config of field level generators.

  private static final int DEFAULT_STRING_LENGTH = 8;
  static final int DEFAULT_LIST_SIZE = 8;

  /**
   * default Integer Type range.
   */
  private static final int INTEGER_START = 0;
  private static final int INTEGER_END = Integer.MAX_VALUE;

  /**
   * default Long Type range.
   */
  private static final long LONG_START = 0L;
  private static final long LONG_END = Long.MAX_VALUE;

  /**
   * default Double Type range.
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
        .put(boolean.class, booleanGenerator())
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
        .build();
  }

  private final Map<Class, ObjectGenerator> generators = new HashMap<>();

  @Override
  public void register(ObjectGenerator objectGenerator) {
    Objects.requireNonNull(objectGenerator);
    Class<?> targetClazz = objectGenerator.getTargetType();
    if (generators.containsKey(targetClazz)) {
      throw new GeneratorException("One ObjectGenerator has been registered for target class: "
          + targetClazz.getName());
    }
    objectGenerator.setObjectMockContext(this);
    generators.put(targetClazz, objectGenerator);
  }

  @Override
  public boolean hasGeneratorFor(Class<?> clazz) {
    return generators.containsKey(clazz);
  }

  @Override
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

  @Override
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

  @Override
  public <T> T generate(Class<?> clazz, AbstractGenerator<T> generator) {
    if (clazz == null && generator.isDynamicTypeGenerator()) {
      throw new GeneratorException("Parameter clazz is mandatory"
          + " when generator has @DynamicTypeGenerator annotation.");
    }
    generator.setObjectMockContext(this);
    return generator.generate(clazz);
  }

  @Override
  public Optional<ObjectGenerator> getObjectGenerator(Class<?> targetClazz) {
    Objects.requireNonNull(targetClazz, "null-pointer parameter");
    return Optional.ofNullable(generators.get(targetClazz));
  }

  @Override
  public ObjectMockContext copy() {
    RealObjectMockContext ctx = new RealObjectMockContext();
    for (Map.Entry<Class, ObjectGenerator> entry : this.generators.entrySet()) {
      ObjectGenerator generator = entry.getValue().copy();
      generator.setObjectMockContext(ctx);
      ctx.generators.put(generator.getTargetType(), generator);
    }
    return ctx;
  }

  @Override
  public ObjectMockContext createVirtualContext() {
    return new VirtualObjectMockContext(this);
  }

}
