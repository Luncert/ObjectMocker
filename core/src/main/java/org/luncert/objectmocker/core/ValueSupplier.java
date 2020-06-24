package org.luncert.objectmocker.core;

/**
 * Function interface using to provide static value for generator.
 * @author Luncert
 */
@FunctionalInterface
public interface ValueSupplier {

  /**
   * Provide a specify value.
   * @return Object
   * @throws Exception java.lang.Exception
   */
  Object get() throws Exception;
}
