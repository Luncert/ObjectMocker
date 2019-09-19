package org.luncert.objectmocker.core;

@FunctionalInterface
public interface ObjectSupplier<T> {

  T getObject(ObjectMockContext context, Class<?> clazz);
}
