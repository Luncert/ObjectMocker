package org.luncert.objectmocker.core;

public abstract class AbstractObjectGenerator<T> extends AbstractGenerator<T> {

  // target type to generate
  private final Class<T> targetType;
  
  public AbstractObjectGenerator(Class<T> targetType) {
    this.targetType = targetType;
  }
  
  public Class<T> getTargetType() {
    return targetType;
  }
  
  public T generate(Class<?> clazz) {
    throw new UnsupportedOperationException();
  }
  
  public abstract T generate(String...ignores);
}
