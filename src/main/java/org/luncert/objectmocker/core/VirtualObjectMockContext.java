package org.luncert.objectmocker.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.luncert.objectmocker.exception.GeneratorException;

class VirtualObjectMockContext extends AbstractObjectMockContext {

  private ObjectMockContext parentContext;

  private final Map<Class, ObjectGenerator> generators = new HashMap<>();

  VirtualObjectMockContext(ObjectMockContext parentContext) {
    this.parentContext = parentContext;
  }

  @Override
  public void register(ObjectGenerator objectGenerator) {
    Objects.requireNonNull(objectGenerator);
  
    Class<?> targetClazz = objectGenerator.getTargetType();
    if (isConfiguredClass(targetClazz)) {
      throw new GeneratorException("Another ObjectGenerator has been registered for target class: "
          + targetClazz.getName());
    }
  
    objectGenerator.setObjectMockContext(this);
    generators.put(targetClazz, objectGenerator);
  }

  @Override
  public boolean isConfiguredClass(Class<?> clazz) {
    return generators.containsKey(clazz) || parentContext.isConfiguredClass(clazz);
  }

  @Override
  public <T> T generate(Class<T> clazz, String... tmpIgnores) {
    ObjectGenerator generator = generators.get(clazz);
    if (generator != null) {
      return clazz.cast(generator.generate(tmpIgnores));
    } else {
      return parentContext.generate(clazz);
    }
  }

  @Override
  public <T> T generate(Class<T> clazz, ObjectGeneratorExtender extender, String... tmpIgnores) {
    ObjectGenerator originalGenerator = generators.get(clazz);
    
    if (originalGenerator != null) {
      ObjectGenerator generator;
      
      try {
        generator = extender.extendObjectGenerator(originalGenerator);
      } catch (Exception e) {
        throw new GeneratorException(e);
      }
      
      checkExtenderReturn(originalGenerator, generator);
      
      return clazz.cast(generator.generate(tmpIgnores));
    }
    
    return parentContext.generate(clazz, extender, tmpIgnores);
  }

  @Override
  public Optional<ObjectGenerator> getObjectGenerator(Class<?> targetClazz) {
    Objects.requireNonNull(targetClazz);
    
    ObjectGenerator generator = generators.get(targetClazz);
    if (generator != null) {
      return Optional.of(generator);
    }
    
    Optional<ObjectGenerator> optional = parentContext.getObjectGenerator(targetClazz);
    if (optional.isPresent()) {
      generator = optional.get();
      generator = new ObjectGeneratorProxy(generator);
      generator.setObjectMockContext(this);
      generators.put(targetClazz, generator);
      return Optional.of(generator);
    }
    
    return Optional.empty();
  }

  @Override
  public ObjectMockContext createChildContext() {
    return new VirtualObjectMockContext(this);
  }
}
