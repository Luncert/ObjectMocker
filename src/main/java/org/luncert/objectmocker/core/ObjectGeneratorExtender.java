package org.luncert.objectmocker.core;

@FunctionalInterface
public interface ObjectGeneratorExtender {

  /**
   * Extend basic ObjectGenerator.
   * @param generator basic ObjectGenerator, should not be modified in this call
   * @return new ObjectGenerator
   */
  ObjectGenerator extendObjectGenerator(final ObjectGenerator generator) throws Exception;
}
