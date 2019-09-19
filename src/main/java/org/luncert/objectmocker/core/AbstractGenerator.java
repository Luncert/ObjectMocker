package org.luncert.objectmocker.core;

import org.luncert.objectmocker.annotation.DynamicTypeGenerator;
import org.luncert.objectmocker.exception.GeneratorException;

import java.util.Objects;

public abstract class AbstractGenerator<T> implements IObjectMockContextAware {

  protected ObjectMockContext context;
  private ObjectSupplier<T> supplier;
  private boolean dynamicTypeGenerator;

  public AbstractGenerator(ObjectSupplier<T> supplier) {
    Objects.requireNonNull(supplier);
    this.supplier = supplier;

    dynamicTypeGenerator = getClass().getAnnotation(DynamicTypeGenerator.class) != null;
  }

  @Override
  public void setObjectMockContext(ObjectMockContext context) {
    this.context = context;
  }

  /**
   * Invoke ObjectSupplier to generate object.
   * @return generated object
   * @throws GeneratorException generating exception
   */
  public T generate(Class<?> clazz) throws GeneratorException {
    return supplier.getObject(context, clazz);
  }

  boolean isDynamicTypeGenerator() {
    return dynamicTypeGenerator;
  }
}
