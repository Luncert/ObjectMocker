package org.luncert.objectmocker.core;

class NoFieldScanningObjectGenerator<T> extends AbstractObjectGenerator<T> {
  
  private NoFieldScanningObjectSupplier<T> supplier;
  
  NoFieldScanningObjectGenerator(Class<T> clazz, NoFieldScanningObjectSupplier<T> supplier) {
    super(clazz);
    this.supplier = supplier;
  }
  
  @Override
  public T generate(String...ignores) {
    return supplier.getObject();
  }
}
