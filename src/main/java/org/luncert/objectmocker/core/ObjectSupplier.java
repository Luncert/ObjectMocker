package org.luncert.objectmocker.core;

/**
 * @author Luncert
 * @param <T>
 */
@FunctionalInterface
public interface ObjectSupplier<T> {

  /**
   * generate an object.
   * @param context ObjectMockContext
   * @param clazz target class
   * @return T
   */
  T getObject(ObjectMockContext context, Class<?> clazz);
}
