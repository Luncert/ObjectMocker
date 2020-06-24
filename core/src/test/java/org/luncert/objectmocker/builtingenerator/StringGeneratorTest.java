package org.luncert.objectmocker.builtingenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.AbstractGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(JUnit4.class)
public class StringGeneratorTest {

  @Test
  public void withLength() {
    int len = 64;
    AbstractGenerator<String> generator = BuiltinGeneratorBuilder.stringGenerator(len);
    String value = generator.generate(null);
    Assert.assertNotNull(value);
    Assert.assertEquals(len, value.length());
  }

  @Test
  public void nonPositiveLength() {
    try {
      BuiltinGeneratorBuilder.stringGenerator(0);
      Assert.fail("Catch no exception");
    } catch (IllegalArgumentException e) {
      // pass
    }
  }

  @Test
  public void rangeFrom() {
    String[] stringValues = new String[]{"Tom", "Joy", "Lucy"};
    Set<String> set = new HashSet<>(Arrays.asList(stringValues));
    AbstractGenerator<String> generator = BuiltinGeneratorBuilder.stringGenerator(stringValues);
    String value = generator.generate(null);
    Assert.assertTrue(set.contains(value));
  }

  @Test
  public void rangeFromSingletonArray() {
    String testValue = "Tom";
    AbstractGenerator<String> generator = BuiltinGeneratorBuilder.stringGenerator(testValue);
    String value = generator.generate(null);
    Assert.assertEquals(testValue, value);
  }

  @Test
  public void rangeFromEmptyArray() {
    try {
      BuiltinGeneratorBuilder.stringGenerator();
      Assert.fail("Catch no exception");
    } catch (IllegalArgumentException e) {
      // pass
    }
  }
}
