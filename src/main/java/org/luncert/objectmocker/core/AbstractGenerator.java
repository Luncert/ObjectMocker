package org.luncert.objectmocker.core;

import org.luncert.objectmocker.exception.GeneratorException;

public abstract class AbstractGenerator<T> {

  public abstract T generate(Class<?> clazz) throws GeneratorException;
}
