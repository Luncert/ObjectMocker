package org.luncert.objectmocker.core;

/**
 * @author Luncert
 */
@FunctionalInterface
public interface ObjectGeneratorModifier {

  /**
   * modify ObjectGenerator
   * @param generator ObjectGenerator
   * @throws Exception java.lang.Exception
   */
  void accept(ObjectGenerator generator) throws Exception;
}
