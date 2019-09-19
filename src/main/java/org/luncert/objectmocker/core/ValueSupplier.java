package org.luncert.objectmocker.core;

@FunctionalInterface
public interface ValueSupplier {

  Object get() throws Exception;
}
