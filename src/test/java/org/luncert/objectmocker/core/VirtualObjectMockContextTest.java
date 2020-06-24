package org.luncert.objectmocker.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.ObjectMocker;

@RunWith(JUnit4.class)
public class VirtualObjectMockContextTest {
  
  public static class TestA {
    protected String field1;
    protected int field2;
  }
  
  public static class TestB extends TestA {
    private String field1;
    private long field3;
  }
  
  @Test
  public void test() {
    ObjectMockContext rootCtx = ObjectMocker.context()
        .register(ObjectGenerator.builder(TestA.class)
            .build())
        .create();
    
    ObjectMockContext childCtx = rootCtx.createChildContext();
    childCtx.register(ObjectGenerator.builder(TestB.class)
        .build());
    
    TestB testB = childCtx.generate(TestB.class);
    return;
  }
}
