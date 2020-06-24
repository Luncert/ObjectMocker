package org.luncert.objectmocker.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.exception.GeneratorException;

import java.util.Set;

@RunWith(JUnit4.class)
public class ObjectGeneratorProxyTest {
  
  public static class TestA {
    private String name;
    private int age;
    private String address;
  }
  
  @Test
  public void testModifyIgnores() {
    ObjectGenerator client = ObjectGenerator.builder(TestA.class)
        .addIgnores("age")
        .build();
    
    ObjectGenerator proxy = new ObjectGeneratorProxy(client);
    Assert.assertTrue(proxy.hasIgnore("age"));
    
    proxy.addIgnores("name");
    Assert.assertFalse(client.hasIgnore("name"));
    
    proxy.removeIgnores("age");
    Assert.assertFalse(proxy.hasIgnore("age"));
    Assert.assertTrue(client.hasIgnore("age"));
    
    Set<String> ignores = proxy.getIgnores();
    Assert.assertEquals(1, ignores.size());
    Assert.assertTrue(ignores.contains("name"));
  }
  
  @Test
  public void testGenerate() throws Exception {
    ObjectGenerator client = ObjectGenerator.builder(TestA.class)
        .addIgnores("age")
        .field("address", () -> "TEST_ADDRESS")
        .build();
  
    ObjectGenerator proxy = new ObjectGeneratorProxy(client);
    proxy.setGenerator("age", () -> Integer.MAX_VALUE);
    proxy.removeIgnores("age");
    
    TestA obj = (TestA) proxy.generate("name");
    Assert.assertNotNull(obj);
    Assert.assertNull(obj.name);
    Assert.assertEquals(Integer.MAX_VALUE, obj.age);
    Assert.assertEquals("TEST_ADDRESS", obj.address);
  }
}
