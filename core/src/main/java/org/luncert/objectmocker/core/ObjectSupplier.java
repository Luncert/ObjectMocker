package org.luncert.objectmocker.core;

/**
 * Function interface using to provide instance for customizing generator.
 * @author Luncert
 * @param <T> instance type
 */
@FunctionalInterface
public interface ObjectSupplier<T> {

  /**
   * Generate an object.
   * @param context ObjectMockContext
   * @param clazz target class
   * @return T
   */
  T getObject(ObjectMockContext context, Class<?> clazz);
}
