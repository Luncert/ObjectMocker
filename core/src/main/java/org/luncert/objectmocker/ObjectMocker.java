package org.luncert.objectmocker;

import org.luncert.objectmocker.core.ObjectGenerator;
import org.luncert.objectmocker.core.ObjectMockContext;
import org.luncert.objectmocker.core.RealObjectMockContext;

/**
 * ObjectMockContext builder.
 * @author Luncert
 */
public class ObjectMocker {

  private ObjectMocker() {
  }

  public static ObjectMockContextBuilder context() {
    return new ObjectMockContextBuilder();
  }

  public static class ObjectMockContextBuilder {
    private RealObjectMockContext context = new RealObjectMockContext();

    private ObjectMockContextBuilder() {
    }

    /**
     * Register ObjectGenerator.
     * @param objectGenerator ObjectGenerator
     */
    public ObjectMockContextBuilder register(ObjectGenerator objectGenerator) {
      context.register(objectGenerator);
      return this;
    }

    /**
     * Create ObjectMockContext.
     * @return ObjectMockContext
     */
    public ObjectMockContext create() {
      return context;
    }
  }
}
