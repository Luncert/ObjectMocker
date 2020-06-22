package org.luncert.objectmocker.core;

/**
 * Function interface using to modify registered ObjectGenerator.
 * @author Luncert
 */
@FunctionalInterface
public interface ObjectGeneratorModifier {

  /**
   * Modify ObjectGenerator.
   * @param generator ObjectGenerator
   * @throws Exception java.lang.Exception
   */
  void accept(ObjectGenerator generator) throws Exception;
}
