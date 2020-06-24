package org.luncert.objectmocker.core;

import org.luncert.objectmocker.exception.GeneratorException;

public abstract class AbstractObjectMockContext implements ObjectMockContext {
  
  public <T> T generate(AbstractGenerator<T> generator) {
    if (generator.isDynamicTypeGenerator()) {
      throw new GeneratorException("generator must be not DynamicTypeGenerator");
    }
    
    return generator.generate(null);
  }
  
  public <T> T generate(AbstractGenerator<T> generator, Class<?> elementType) {
    if (!generator.isDynamicTypeGenerator()) {
      throw new GeneratorException("generator must be extended from DynamicTypeGenerator");
    }
    
    if (elementType == null) {
      throw new GeneratorException("element type is mandatory");
    }
    
    generator.setObjectMockContext(this);
    return generator.generate(elementType);
  }
  
  void checkExtenderReturn(ObjectGenerator originalGenerator, ObjectGenerator generator) {
    if (generator == null) {
      throw new GeneratorException("return value of extender must be non-null");
    }
    
    if (generator.hashCode() == originalGenerator.hashCode()) {
      throw new GeneratorException("return value of extender must be a new instance");
    }
  }
}
