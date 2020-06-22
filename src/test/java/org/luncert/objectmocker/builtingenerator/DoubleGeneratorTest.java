package org.luncert.objectmocker.builtingenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.AbstractGenerator;

@RunWith(JUnit4.class)
public class DoubleGeneratorTest {

  @Test
  public void defaultValue() {
    double defaultValue = -1.2d;
    AbstractGenerator<Double> generator = BuiltinGeneratorBuilder.doubleGenerator(defaultValue);
    Double value = generator.generate(null);
    Assert.assertEquals(Double.valueOf(defaultValue), value);
  }

  @Test
  public void rangeFrom() {
    double start = -1.2d, end = 1000000000d;
    AbstractGenerator<Double> generator = BuiltinGeneratorBuilder.doubleGenerator(start, end);
    Double value = generator.generate(null);
    Assert.assertTrue(value >= start);
    Assert.assertTrue(value < end);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void givenInvalidRange() {
    double start = 0d, end = 0d;
    BuiltinGeneratorBuilder.doubleGenerator(start, end);
  }
}
