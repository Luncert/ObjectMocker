package org.luncert.objectmocker.core;

/**
 * Function interface using to extend registered ObjectGenerator.
 * @author Luncert
 */
@FunctionalInterface
public interface ObjectGeneratorExtender {

  /**
   * Extend basic ObjectGenerator.
   * @param basicGenerator basic ObjectGenerator, should not be modified in this call
   * @return new ObjectGenerator
   * @throws Exception java.lang.Exception
   */
  ObjectGenerator extendObjectGenerator(final ObjectGenerator basicGenerator) throws Exception;
}
