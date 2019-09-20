package org.luncert.objectmocker;

import java.util.Map;

public interface Generator<T> {

  T generate(Map config);

}