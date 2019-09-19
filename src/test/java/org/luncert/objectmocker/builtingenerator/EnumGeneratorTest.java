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
public class EnumGeneratorTest {

  private enum TestEnum {
    A, B, C
  }

  public static class TestClassEnumField {
    private TestEnum enumField;
  }

  @Test
  @SuppressWarnings("unchecked")
  public void defaultValue() {
    AbstractGenerator generator = BuiltinGeneratorBuilder.enumGenerator(TestEnum.A);
    Object value = generator.generate(null);
    Assert.assertEquals(TestEnum.A, value);
  }

  @Test
  public void rangeFrom() {
    AbstractGenerator generator = BuiltinGeneratorBuilder.enumGenerator(TestEnum.values());
    Object value = generator.generate(null);
    Assert.assertNotNull(value);
    Set<TestEnum> testEnums = new HashSet<>(Arrays.asList(TestEnum.values()));
    TestEnum e = (TestEnum) value;
    Assert.assertTrue(testEnums.contains(e));
  }

  @Test
  public void emptyEnum() {
    try {
      BuiltinGeneratorBuilder.enumGenerator(new TestEnum[]{});
      Assert.fail("Catch no exception");
    } catch (IllegalArgumentException e) {
      // pass
    }
  }
}
