package org.luncert.objectmocker.builtingenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.AbstractGenerator;

import java.time.ZonedDateTime;

@RunWith(JUnit4.class)
public class ZonedDateTimeGeneratorTest {

  @Test
  public void successCase() {
    AbstractGenerator<ZonedDateTime> generator = BuiltinGeneratorBuilder.zonedDateTimeGenerator();
    ZonedDateTime value = generator.generate(null);
    Assert.assertNotNull(value);
  }
}
