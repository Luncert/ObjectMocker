package org.luncert.objectmocker.core;

/**
 * @author Luncert
 */
public interface IObjectMockContextAware {

  /**
   * used to obtain ObjectMockContext
   * @param objectMockContext ObjectMockContext
   */
  void setObjectMockContext(ObjectMockContext objectMockContext);
}
