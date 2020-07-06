package org.luncert.objectmocker.core;

import org.luncert.objectmocker.exception.GeneratorException;

/**
 * AbstractGenerator.
 * @author Luncert
 * @param <T> target type to generate.
 */
public abstract class AbstractGenerator<T> implements IObjectMockContextAware {

  protected ObjectMockContext context;

  @Override
  public void setObjectMockContext(ObjectMockContext context) {
    this.context = context;
  }

  /**
   * Invoke ObjectSupplier to generate object.
   * @return generated object
   * @throws GeneratorException generating exception
   */
  public abstract T generate(Class<?> clazz) throws GeneratorException;
}
