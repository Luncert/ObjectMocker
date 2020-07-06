package org.luncert.objectmocker.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.luncert.objectmocker.exception.GeneratorException;

class VirtualObjectMockContext extends AbstractObjectMockContext {
  
  private ObjectMockContext parentContext;
  
  private final Map<Class, AbstractObjectGenerator> generators = new HashMap<>();
  
  VirtualObjectMockContext(ObjectMockContext parentContext) {
    this.parentContext = parentContext;
  }
  
  @Override
  public void register(AbstractObjectGenerator objectGenerator) {
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
    AbstractObjectGenerator generator = generators.get(clazz);
    if (generator != null) {
      return clazz.cast(generator.generate(tmpIgnores));
    } else {
      return parentContext.generate(clazz);
    }
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <T> T generate(Class<T> clazz, ObjectGeneratorExtender<T> extender, String... tmpIgnores) {
    AbstractObjectGenerator<T> generator = generators.get(clazz);
    
    if ((generator instanceof ObjectGenerator)) {
      ObjectGenerator original = (ObjectGenerator) generator;
      ObjectGenerator extended;
      
      try {
        extended = extender.extendObjectGenerator(original);
      } catch (Exception e) {
        throw new GeneratorException(e);
      }
      
      checkExtenderReturn(original, extended);
      return clazz.cast(generator.generate(tmpIgnores));
    }
    
    return parentContext.generate(clazz, extender, tmpIgnores);
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <T> Optional<AbstractObjectGenerator<T>> getObjectGenerator(Class<T> targetClazz) {
    Objects.requireNonNull(targetClazz);
    
    AbstractObjectGenerator<T> generator = generators.get(targetClazz);
    if (generator != null) {
      return Optional.of(generator);
    }
    
    Optional<AbstractObjectGenerator<T>> optional = parentContext.getObjectGenerator(targetClazz);
    if (optional.isPresent()) {
      generator = optional.get();
      // if the generator is an instance of ObjectGenerator,
      // we will create a proxy for it and return the proxy
      if (generator instanceof ObjectGenerator) {
        ObjectGenerator<T> proxy = new ObjectGeneratorProxy<>((ObjectGenerator<T>) generator);
        proxy.setObjectMockContext(this);
        generators.put(targetClazz, proxy);
        return Optional.of(proxy);
      } else {
        return Optional.of(generator);
      }
    }
    
    return Optional.empty();
  }
  
  @Override
  public ObjectMockContext createChildContext() {
    return new VirtualObjectMockContext(this);
  }
}
