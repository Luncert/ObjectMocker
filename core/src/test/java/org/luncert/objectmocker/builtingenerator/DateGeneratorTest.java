package org.luncert.objectmocker.builtingenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.AbstractGenerator;

import java.util.Date;

@RunWith(JUnit4.class)
public class DateGeneratorTest {

  @Test
  public void successCase() {
    AbstractGenerator<Date> generator = BuiltinGeneratorBuilder.dateGenerator();
    Date date = generator.generate(null);
    Assert.assertNotNull(date);
  }
}
