package org.luncert.objectmocker.builtingenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.AbstractGenerator;

@RunWith(JUnit4.class)
public class IntegerGeneratorTest {

  @Test
  public void defaultValue() {
    int defaultValue = -1;
    AbstractGenerator<Integer> generator = BuiltinGeneratorBuilder.integerGenerator(defaultValue);
    Integer value = generator.generate(null);
    Assert.assertEquals(Integer.valueOf(defaultValue), value);
  }

  @Test
  public void rangeFrom() {
    int start = -1, end = 1000000;
    AbstractGenerator<Integer> generator = BuiltinGeneratorBuilder.integerGenerator(start, end);
    Integer value = generator.generate(null);
    Assert.assertTrue(value >= start);
    Assert.assertTrue(value < end);
  }

  @Test
  public void invalidParameter() {
    try {
      BuiltinGeneratorBuilder.integerGenerator(2, 2);
      Assert.fail("Catch no exception");
    } catch (IllegalArgumentException e) {
      // pass
    }
  }
}
