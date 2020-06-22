package org.luncert.objectmocker.core;

/**
 * Declare target generator is used for generate dynamic type, e.g. {@code List<String>;}.
 * @param <T> target type
 */
public class DynamicTypeGenerator<T> extends AbstractGenerator<T> {
  
  /**
   * Abstract parent class for all specified type generator.
   *
   * @param supplier ObjectSupplier instructs how to generate value for specify type.
   */
  public DynamicTypeGenerator(ObjectSupplier<T> supplier) {
    super(supplier);
  }
}
