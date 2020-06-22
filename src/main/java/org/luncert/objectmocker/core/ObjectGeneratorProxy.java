package org.luncert.objectmocker.core;

import org.luncert.objectmocker.exception.GeneratorException;

final class ObjectGeneratorProxy extends ObjectGenerator {
  
  ObjectGeneratorProxy(ObjectGenerator objectGenerator) {
    super(objectGenerator.getTargetType());
    
    if (objectGenerator instanceof ObjectGeneratorProxy) {
      throw new GeneratorException("target instance to be proxy must be not an instance of ObjectGeneratorProxy");
    }
  }
}
