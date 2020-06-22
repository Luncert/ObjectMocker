package org.luncert.objectmocker.core;

/**
 * IObjectMockContextAware, used to obtain ObjectMockContext in object generator.
 * @author Luncert
 */
public interface IObjectMockContextAware {

  /**
   * Used to obtain RealObjectMockContext.
   * @param objectMockContext RealObjectMockContext
   */
  void setObjectMockContext(ObjectMockContext objectMockContext);
}
