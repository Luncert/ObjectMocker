package org.luncert.objectmocker.builtingenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.AbstractGenerator;

import java.math.BigDecimal;

@RunWith(JUnit4.class)
public class BigDecimalGeneratorTest {

  @Test
  public void defaultValue() {
    double defaultValue = 1.2d;
    AbstractGenerator<BigDecimal> generator = BuiltinGeneratorBuilder.bigDecimalGenerator(defaultValue);
    BigDecimal value = generator.generate(null);
    Assert.assertEquals(BigDecimal.valueOf(defaultValue), value);
  }

  @Test
  public void rangeFrom() {
    double start = 1.2d, end = 1000000000d;
    AbstractGenerator<BigDecimal> generator = BuiltinGeneratorBuilder.bigDecimalGenerator(start, end);
    BigDecimal value = generator.generate(null);
    Assert.assertTrue(value.compareTo(BigDecimal.valueOf(start)) >= 0);
    Assert.assertTrue(value.compareTo(BigDecimal.valueOf(end)) < 0);
  }
}
