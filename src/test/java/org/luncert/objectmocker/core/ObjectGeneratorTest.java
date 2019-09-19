package org.luncert.objectmocker.core;

import lombok.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.ObjectMocker;
import org.luncert.objectmocker.exception.GeneratorException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.luncert.objectmocker.core.BuiltinGeneratorBuilder.*;

@RunWith(JUnit4.class)
public class ObjectGeneratorTest {

  private static enum TestEnum {
    A, B, C
  }

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
    private TestEnum enumField;
  }

  @Test
  public void basicSuccessCase() throws NoSuchFieldException {
    ObjectMockContext context = ObjectMocker.context()
          .register(ObjectGenerator.builder(TestClass.class).build())
          .create();
    context.generate(TestClass.class);
  }
}
