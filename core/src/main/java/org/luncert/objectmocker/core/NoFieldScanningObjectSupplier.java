package org.luncert.objectmocker.core;

@FunctionalInterface
public interface NoFieldScanningObjectSupplier<T> {
  
  T getObject();
}
