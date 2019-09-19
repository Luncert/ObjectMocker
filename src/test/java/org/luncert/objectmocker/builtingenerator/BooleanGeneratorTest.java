package org.luncert.objectmocker.builtingenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.AbstractGenerator;

@RunWith(JUnit4.class)
public class BooleanGeneratorTest {

  @Test
  public void successCase() {
    AbstractGenerator<Boolean> generator = BuiltinGeneratorBuilder.booleanGenerator();
    Boolean value = generator.generate(null);
    Assert.assertNotNull(value);
  }
}
