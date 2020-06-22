package org.luncert.objectmocker;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.core.ObjectGenerator;
import org.luncert.objectmocker.core.ObjectMockContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.enumGenerator;

@RunWith(JUnit4.class)
public class ObjectMockerTest {

  private enum TestEnum {
    A, B, C
  }

  @Data
  public static class TestClass {
    protected BigDecimal bigDecimalField;
    public boolean booleanField;
    private Date dateField;
    private Double doubleField;
    private int integerField;
    private long longField;
    private String stringField;
    private ZonedDateTime zonedDateTimeField;
    private String shouldBeIgnored;
    private TestEnum enumField;
    private String stringUuidField;
  }

  @Test
  public void basicSuccessCase() {
    ObjectMockContext context = ObjectMocker.context()
          .register(ObjectGenerator.builder(TestClass.class).build())
          .create();
    context.generate(TestClass.class);
  }

  public static class LevelA {
    private String name;
    private String pos;
    private List<LevelB> items;
  }

  public static class LevelB {
    private int id;
  }

  @Test
  public void mapConfiguredGenerating() throws IOException {
    ObjectMockContext context = ObjectMocker.context()
        .register(ObjectGenerator.builder(LevelA.class).build())
        .register(ObjectGenerator.builder(LevelB.class).build())
        .create();

    Map<String, Object> config = new HashMap<>();
    config.put("name", "ASD");
    config.put("items", Arrays.asList(
        ImmutableMap.builder().put("id", "1").build(),
        ImmutableMap.builder().put("id", "2").build()
    ));

    LevelA ins = context.generate(LevelA.class, config);
    Assert.assertEquals("ASD", ins.name);
    Assert.assertNotNull(ins.pos);
    Assert.assertEquals(2, ins.items.size());
    Assert.assertEquals(1, ins.items.get(0).id);
    Assert.assertEquals(2, ins.items.get(1).id);
  }

  @Test
  public void copyContextAndModifyGenerator() throws Exception {
    ObjectMockContext context = ObjectMocker.context()
        .register(ObjectGenerator.builder(TestClass.class).build())
        .create();

    ObjectMockContext copiedCtx = context.copy();
    copiedCtx.modifyObjectGenerator(TestClass.class, generator ->
        generator.addIgnores("shouldBeIgnored"));

    TestClass ins = context.generate(TestClass.class);
    Assert.assertNotNull(ins.getShouldBeIgnored());

    ins = copiedCtx.generate(TestClass.class);
    Assert.assertNull(ins.getShouldBeIgnored());
  }

  @Test
  public void extendRegisteredGenerator() throws NoSuchFieldException {
    ObjectMockContext context = ObjectMocker.context()
        .register(ObjectGenerator.builder(TestClass.class)
            .field("enumField", enumGenerator(new TestEnum[]{TestEnum.B, TestEnum.C}))
            .build())
        .create();

    TestClass ins = context.generate(TestClass.class);
    Assert.assertNotEquals(TestEnum.A, ins.getEnumField());

    ins = context.generate(TestClass.class, basicGenerator ->
      ObjectGenerator.builder(TestClass.class)
          .field("enumField", enumGenerator(TestEnum.A))
          .extend(basicGenerator)
    );
    Assert.assertEquals(TestEnum.A, ins.getEnumField());
  }

  @Test
  public void provideCustomizedGenerator() throws NoSuchFieldException {
    ObjectMockContext context = ObjectMocker.context()
        .register(ObjectGenerator.builder(TestClass.class)
            .field("stringUuidField", (ctx, clz) -> UUID.randomUUID().toString())
            .build())
        .create();

    TestClass ins = context.generate(TestClass.class);
    UUID id = UUID.fromString(ins.getStringUuidField());
    Assert.assertNotNull(id);
  }

  @Test
  public void createVirtualContext() throws Exception {
    ObjectMockContext context = ObjectMocker.context()
        .register(ObjectGenerator.builder(TestClass.class).build())
        .create();

    ObjectMockContext virtualCtx = context.createVirtualContext();
    virtualCtx.modifyObjectGenerator(TestClass.class, generator -> {
      generator.addIgnores("shouldBeIgnored");
      generator.setGenerator("stringUuidField", (ctx, clz) -> "X801EF");
    });

    TestClass value = context.generate(TestClass.class);
    Assert.assertNotNull(value.getShouldBeIgnored());
    Assert.assertEquals(8, value.getStringUuidField().length());

    value = virtualCtx.generate(TestClass.class);
    Assert.assertNull(value.getShouldBeIgnored());
    Assert.assertEquals("X801EF", value.getStringUuidField());
  }
}
