package org.luncert.objectmocker.core;

import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.exception.GeneratorException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

@RunWith(JUnit4.class)
public class ObjectGeneratorTest {

  @Data
  private static class TestClass {
    protected BigDecimal bigDecimalField;
    public boolean booleanField;
    private Date dateField;
    private double doubleField;
    private int integerField;
    private long longField;
    private String stringField;
    private ZonedDateTime zonedDateTimeField;
    private String shouldBeIgnored;
  }

  @Test
  public void successCase() {
    TestClass ins = ObjectGenerator.generate(TestClass.class, "shouldBeIgnored");
    System.out.println(ins);
  }

  @Data
  private static class ComplexClass {
    private TestClass customTypeField;
  }

  @Test
  public void nonSupportedType() {
    try {
      ObjectGenerator.generate(ComplexClass.class, "shouldBeIgnored");
      Assert.fail("Catch no exception");
    } catch (GeneratorException e) {
      Assert.assertNull("Incorrect exception type", e.getCause());
    }
  }
}
