package org.luncert.objectmocker.core;

import org.luncert.objectmocker.exception.GeneratorException;

import java.util.Objects;

public class LambdaBasedGenerator<T> extends AbstractGenerator<T> {
  
  private ObjectSupplier<T> supplier;
  
  /**
   * Abstract parent class for all specified type generator.
   * @param supplier ObjectSupplier instructs how to generate value for specify type.
   */
  public LambdaBasedGenerator(ObjectSupplier<T> supplier) {
    Objects.requireNonNull(supplier);
    this.supplier = supplier;
  }
  
  /**
   * Invoke ObjectSupplier to generate object.
   * @return generated object
   * @throws GeneratorException generating exception
   */
  public T generate(Class<?> clazz) throws GeneratorException {
    return supplier.getObject(context, clazz);
  }
}
