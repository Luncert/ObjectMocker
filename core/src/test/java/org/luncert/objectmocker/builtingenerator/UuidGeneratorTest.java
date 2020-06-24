package org.luncert.objectmocker.builtingenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.AbstractGenerator;

import java.util.UUID;

@RunWith(JUnit4.class)
public class UuidGeneratorTest {

  @Test
  public void successCase() {
    AbstractGenerator<UUID> generator = BuiltinGeneratorBuilder.uuidGenerator();
    UUID value = generator.generate(null);
    Assert.assertNotNull(value);
  }
}
