package org.luncert.objectmocker.core;

/**
 * @author Luncert
 */
@FunctionalInterface
public interface ValueSupplier {

  /**
   * provide a specify value.
   * @return Object
   * @throws Exception java.lang.Exception
   */
  Object get() throws Exception;
}
