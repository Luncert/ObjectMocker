package org.luncert.objectmocker.core;

/**
 * @author Luncert
 */
public interface IObjectMockContextAware {

  /**
   * used to obtain RealObjectMockContext
   * @param objectMockContext RealObjectMockContext
   */
  void setObjectMockContext(ObjectMockContext objectMockContext);
}
