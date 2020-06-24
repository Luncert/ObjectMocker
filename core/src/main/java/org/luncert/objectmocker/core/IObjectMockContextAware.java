package org.luncert.objectmocker.core;

/**
 * IObjectMockContextAware, used to obtain ObjectMockContext in object generator.
 * @author Luncert
 */
public interface IObjectMockContextAware {

  /**
   * Used to obtain RealObjectMockContext.<br/>
   * <li>{@link ObjectGenerator#setObjectMockContext(ObjectMockContext)}:Set context to object generator -> set context to field generator.</li>
   * <li>{@link AbstractObjectMockContext#generate(AbstractGenerator,Class)}:Generate with user provided generator -> set context to this generator.</li>
   * <li>{@link RealObjectMockContext#register(ObjectGenerator)}:Register new generator to context -> set context to new generator.</li>
   * <li>{@link VirtualObjectMockContext#register(ObjectGenerator)}:Register new generator to context -> set context to new generator.</li>
   * <li>{@link VirtualObjectMockContext#getObjectGenerator(Class)}:Proxy target generator -> set context to proxy.</li>
   * @param objectMockContext RealObjectMockContext
   */
  void setObjectMockContext(ObjectMockContext objectMockContext);
}
