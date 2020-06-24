package org.luncert.objectmocker.builtingenerator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.ObjectMocker;
import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

import java.util.List;

@RunWith(JUnit4.class)
public class ListGeneratorTest {

  @Test
  public void withLength() {
    int len = 5;
    AbstractGenerator<List> generator = BuiltinGeneratorBuilder.listGenerator(len);
    generator.setObjectMockContext(ObjectMocker.context().create());
    List list = generator.generate(String.class);
    Assert.assertNotNull(list);
    Assert.assertEquals(len, list.size());
    Assert.assertTrue(ArrayUtils.isNotEmpty(list.toArray()));
  }

  @Test
  public void withNonPositiveLength() {
    try {
      BuiltinGeneratorBuilder.listGenerator(0);
      Assert.fail("Catch no exception");
    } catch (IllegalArgumentException e) {
      // pass
    }
  }

  @Test
  public void withElementGenerator() {
    int len = 5;
    ObjectSupplier<Object> supplier = (ctx, clz) -> RandomStringUtils.randomAlphanumeric(12);
    AbstractGenerator<List> generator = BuiltinGeneratorBuilder.listGenerator(len, supplier);
    List list = generator.generate(null);
    Assert.assertNotNull(list);
    Assert.assertEquals(len, list.size());
    Assert.assertTrue(ArrayUtils.isNotEmpty(list.toArray()));
  }

  @Test
  public void withElementGeneratorNonPositiveLength() {
    try {
      BuiltinGeneratorBuilder.listGenerator(0, null);
      Assert.fail("Catch no exception");
    } catch (IllegalArgumentException e) {
      // pass
    }
  }
}
