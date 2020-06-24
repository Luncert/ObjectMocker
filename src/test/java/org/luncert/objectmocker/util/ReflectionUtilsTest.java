package org.luncert.objectmocker.util;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.exception.GeneratorException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RunWith(JUnit4.class)
public class ReflectionUtilsTest {
  
  public static class TestA {
    public String field1;
    protected static String field2;
    private final String field3 = "";
    private static final List<String> field4 = null;
    private Map<String, Integer> field5;
    private Triple<String, Long, Boolean> field6;
  }
  
  @Test
  public void testGetField() throws NoSuchFieldException {
    Assert.assertNotNull(ReflectionUtils.getField(TestA.class, "field1"));
    Assert.assertNotNull(ReflectionUtils.getField(TestA.class, "field2"));
    Assert.assertNotNull(ReflectionUtils.getField(TestA.class, "field3"));
  }
  
  @Test
  public void testGetParameterType() throws NoSuchFieldException {
    Field field = ReflectionUtils.getField(TestA.class, "field4");
    List<Class> types = ReflectionUtils.getParameterType(field);
    Assert.assertEquals(1, types.size());
    Assert.assertEquals(String.class, types.get(0));
    
    field = ReflectionUtils.getField(TestA.class, "field5");
    types = ReflectionUtils.getParameterType(field);
    Assert.assertEquals(2, types.size());
    Assert.assertEquals(String.class, types.get(0));
    Assert.assertEquals(Integer.class, types.get(1));
  
    field = ReflectionUtils.getField(TestA.class, "field6");
    types = ReflectionUtils.getParameterType(field);
    Assert.assertEquals(3, types.size());
    Assert.assertEquals(String.class, types.get(0));
    Assert.assertEquals(Long.class, types.get(1));
    Assert.assertEquals(Boolean.class, types.get(2));
  }
  
  @Test(expected = GeneratorException.class)
  public void testGetParameterTypeError() throws NoSuchFieldException {
    Field field = ReflectionUtils.getField(TestA.class, "field3");
    ReflectionUtils.getParameterType(field);
  }
}
