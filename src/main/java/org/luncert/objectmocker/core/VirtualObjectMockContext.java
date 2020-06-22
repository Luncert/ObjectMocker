package org.luncert.objectmocker.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.luncert.objectmocker.exception.GeneratorException;

class VirtualObjectMockContext implements ObjectMockContext {

  private final Map<Class, ObjectGenerator> modifications = new HashMap<>();
  private RealObjectMockContext realContext;

  VirtualObjectMockContext(RealObjectMockContext realContext) {
    this.realContext = realContext;
  }

  @Override
  public void register(ObjectGenerator objectGenerator) {

  }

  @Override
  public boolean hasGeneratorFor(Class<?> clazz) {
    return realContext.hasGeneratorFor(clazz);
  }

  @Override
  public <T> T generate(Class<T> clazz, String... tmpIgnores) {
    ObjectGenerator mod = modifications.get(clazz);
    if (mod != null) {
      return realContext.generate(clazz, basicGenerator -> {
        ObjectGenerator generator = new ObjectGenerator(clazz,
            mod.getIgnores(),
            mod.getFieldGenerators());

        basicGenerator.getIgnores().forEach(generator::addIgnores);
        Map<Field, AbstractGenerator> fieldGenerators = generator.getFieldGenerators();
        for (Map.Entry<Field, AbstractGenerator> entry :
            basicGenerator.getFieldGenerators().entrySet()) {
          if (!fieldGenerators.containsKey(entry.getKey())) {
            fieldGenerators.put(entry.getKey(), entry.getValue());
          }
        }

        generator.setObjectMockContext(this);
        return generator;
      });
    } else {
      return realContext.generate(clazz);
    }
  }

  @Override
  public <T> T generate(Class<T> clazz, ObjectGeneratorExtender extender, String... tmpIgnores) {
    return realContext.generate(clazz, extender, tmpIgnores);
  }

  @Override
  public <T> T generate(ObjectSupplier<T> supplier) {
    return realContext.generate(supplier);
  }

  @Override
  public <T> T generate(Class<?> clazz, AbstractGenerator<T> generator) {
    return realContext.generate(clazz, generator);
  }

  @Override
  public <T> T generate(Class<T> clazz, Map<String, Object> baseData) throws IOException {
    throw new UnsupportedOperationException("Not supported for now.");
  }

  @Override
  public void modifyObjectGenerator(Class<?> clazz, ObjectGeneratorModifier modifier)
      throws Exception {
    Objects.requireNonNull(clazz);
    if (!realContext.hasGeneratorFor(clazz)) {
      throw new GeneratorException("No generator registered for class %s.", clazz.getName());
    }
    ObjectGenerator generator = modifications.get(clazz);
    if (generator == null) {
      generator = new ObjectGenerator(clazz);
      modifications.put(clazz, generator);
    }
    modifier.accept(generator);
  }

  /**
   * VirtualObjectMockContext doesn't support copy operation.
   * @return
   */
  @Override
  public ObjectMockContext copy() {
    throw new UnsupportedOperationException("Copying is unsupported on VirtualObjectMockContext.");
  }

  @Override
  public ObjectMockContext createVirtualContext() {
    throw new UnsupportedOperationException("Creating virtual context is unsupported "
        + "in VirtualObjectMockContext.");
  }
}
