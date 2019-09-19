package org.luncert.objectmocker.builtingenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.AbstractGenerator;

@RunWith(JUnit4.class)
public class LongGeneratorTest {

  @Test
  public void defaultValue() {
    long defaultValue = -1L;
    AbstractGenerator<Long> generator = BuiltinGeneratorBuilder.longGenerator(defaultValue);
    Long value = generator.generate(null);
    Assert.assertEquals(Long.valueOf(defaultValue), value);
  }

  @Test
  public void rangeFrom() {
    long start = -1L, end = 10000000L;
    AbstractGenerator<Long> generator = BuiltinGeneratorBuilder.longGenerator(start, end);
    Long value = generator.generate(null);
    Assert.assertTrue(value >= start);
    Assert.assertTrue(value < end);
  }

  @Test
  public void invalidParameter() {
    try {
      BuiltinGeneratorBuilder.longGenerator(2L, 2L);
      Assert.fail("Catch no exception");
    } catch (IllegalArgumentException e) {
      // pass
    }
  }
}
