# ObjectMocker

![](https://img.shields.io/badge/language-java-brightgreen.svg?style=plastic) ![](https://img.shields.io/badge/build-passing-brightgreen) ![](https://img.shields.io/badge/coverage-80%25-brightgreen)

## 用法

### 第1步：为目标类型构建一个```ObjectGenerator```

测试类：

```java
@Data
public class Person {
    private String name;
    private String uuid;
    private List<String> phoneList;
    private String deprecatedField;
}
```

接下来使用```ObjectGenerator```提供的构建器：

```java
ObjectGenerator personGenerator = ObjectGenerator.builder(Person.class)
    .addIgnores("deprecatedField")
    .field("uuid", (ctx, cls) -> UUID.randomUUID().toString())
    .field("phoneList", BuiltinGeneratorBuilder.listGenerator(5))
    .build();
```

这里我做了以下动作：

* 调用```ObjectGeneratorBuilder.addIgnores```方法配置```ObjectGenerator```在生成实例时忽略```deprecatedField```字段
* 为```uuid```字段提供自定义的生成器，用于生成字符串类型的UUID
* 为```phoneList```字段提供配置了一个内建的列表生成器，指定生成长度5的```java.util.ArrayList```

对于```name```字段，由于我们没有为它设置一个生成器，所以```ObjectGenerator```将使用**拥有默认配置的**内建字符串生成器，该生成器生成长度8的、仅含字母的随机字符串

我已经为Java里的常用类型提供了一系列内建生成器，每个都有自己的默认配置，比如```ListGenerator```默认生成长度8的列表。

你也可以给这些内建生成器赋予特殊的配置，再绑定到```ObjectGenerator```的某个字段上，我提供了```BuiltinGeneratorBuilder```来帮助你完成这个任务，这个类包含了一些静态方法，每个都能传入配置项然后返回配置过的内建生成器，十分建议你使用静态导入来在你的代码中引入它：

```java
import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.*;
```

这样代码会更简洁。下面是```BuiltinGeneratorBuilder```的使用示例：

```java
ObjectGenerator personGenerator = ObjectGenerator.builder(Person.class)
    .field("phoneList", listGenerator(5))
    .field("stringField", stringGenerator("Tom", "Joy", "Lucy")) // 从字符串数组里随机选择一个值输出
    .field("stringId", stringGenerator("nil"))
    .field("bigDecimalField", bigDecimalGenerator(-1.2d, 1000d)) // 生成处于-1.2~1000范围里的值
    .field("booleanField", booleanGenerator())
    .field("enumField", enumGenertaor(Kinds.Student)) // 使用固定值作为枚举类型的输出
    .build();
```

大多数内建生成器都有```rangeFrom```和```defaultValue```这样的选项

### 第2步：创建```ObjectMockContext```

上一步我们已经创建好了```ObjectGenerator```，要使用它我们还需要创建一个```ObjectMockContext```来把```ObjectGenerator```注册进去，然后就可以使用了，像这样：

```java
ObjectMockContext ctx = ObjectMocker.context()
    .register(personGenerator)
    .create();
Person person = ctx.generate(Person.class);
```

```ObjectMockContext```用于管理创建过的```ObjectGenerator```，有时候你要生成的类可能有个字段是自定义类型的，我们的内建生成器可处理不了它，我只能尝试将这个字段委托给```ObjectMockContext```，因为这里保存着所有你注册过的```ObjectGenerator```，可能就会有某个对象生成器可以这个自定义类型的字段呢？

如果```ObjectMockContext```也处理不了，程序就要报错了：```org.luncert.objectmocker.exception.GeneratorException: No generator registered for class xxx.```所以要确保所有相关的自定义类型都已经注册过一个对象生成器了，注册顺序是无关紧要的。

顺便一提，```ObjectMocker```是```ObjectMockContext```构建器，用它比new对象的方法更简洁。

## 高级用法

### 1.提供一个自定义的字段生成器

想象这样一个场景，你有一个Test Case，需要测试业务代码校验字段的逻辑是否有缺陷，假定字段的正确值为长度12以内、以```"800"```为前缀的字符串，要生成这样的正确值，内建的字符串生成器已经满足不了需求了：

```java
@RunWith(JUnit4.class)
public class XxxTest {

	public static class TestClass {
		private String businessId;
	}

	@Test
	public void testInvalidValue() {
		ObjectMockContext ctx = ObjectMocker.context()
    		.register(ObjectGenerator.builder(Person.class)
    			.field("businessId", (ctx, clz) ->
                      "800" + RandomStringUtils.randomAlphabetic(9))
                 .build())
    		.create();
        TestClass ins = ctx.generate(TestClass.class);
        // ...
        // process generated data
	}
}
```

聚焦于```field```处的lambda表达式上，这个lambda表达式实现了```org.luncert.objectmocker.core.ObjectSupplier```接口，这是一个函数式接口，定义十分简单：

```java
@FunctionalInterface
public interface ObjectSupplier<T> {

  /**
   * generate an object.
   * @param context ObjectMockContext
   * @param clazz target class
   * @return T
   */
  T getObject(ObjectMockContext context, Class<?> clazz);
}
```

看到源码后你可能已经明白了，lambda表达式里的两个入参的含义：

* ```ctx```：```ObjectGenerator```注册的```ObjectMockContext```实例，这个入参你大概率是用不着的。有时你的自定义生成器可能是一个动态类型的生成器（即不知道自己要处理什么类型的数据），典型如```ListGenerator```，列表生成器要生成一个```List```，但它要处理的是```List<E>```里的泛型类型```E```，很多情况下这个```E```都是自定义类型，所以就需要委托给```ObjectMockContext```去处理了。如果你想提供一个```java.util.Map```的生成器的话，可能需要稍微考虑以下这段话（也许这是纯靠API实现不了的😭）。
* ```clz```：我们自定义的字段生成器要处理的字段的类型，在上面的例子里```clz```肯定是```java.lang.String.class```

### 2.如何使用```ObjectMockContext```最恰当呢

在一开始，设计```ObjectMockContext```的主要意图是为了适配测试代码相互隔离的情形，所以我设想的是每个测试类拥有一个context，测试类中的每个测试方法共享这个上下文。

后来在实际编码中我发现，像实体类或者DTO这样的类型，一方面每个字段都需要配置一个字段生成器（对应数据库表定义中对字段长度的设置），这会产生大量配置、注册代码，另一方面它们的生成器常常被多个测试类用到，因为像DTO常常是贯穿整个业务流程的。所以我设想程序员可以添加一个```GlobalObjectMockContext```，运行单例和懒加载，集中注册测试需要的实体类、DTO的对象生成器，像这样：

```java
public final class GlobalObjectMockContext {

  private GlobalObjectMockContext() {
  }

  private static ObjectMockContext CONTEXT;
    /**
   * Provide a basic context with some pre-registered ObjectGenerator.
   *
   * @return ObjectMockContext
   */
  public static ObjectMockContext getInstance() {
    if (CONTEXT == null) {
      synchronized (GlobalObjectMockContext.class) {
        if (CONTEXT == null) {
          CONTEXT = ObjectMocker.context().create();
          try {
            // 初始化CONTEXT，注册需要的对象生成器
          } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
    return CONTEXT;
  }
}
```

这样再在每个测试类中的从```GlobalObjectMockContext```获取获取```ObjectMockContext```实例，这样就避免了在每个测试类中为相同实体类/DTO创建对象生成器，避免了代码冗余。这样做也产生了问题，它违背了设计```ObjectMockContext```的初衷，变成了多个测试类共享context。这个时候就要引入新的拷贝与继承机制，参考接下来的章节。

### 3.拷贝```ObjectMockContext```，拓展/继承已经存在的对象生成器

我们已经有了```GlobalObjectMockContext```，为了避免每个测试类共享一个```ObjectMockContext```实例，我为```ObjectMockContext```实现了```copy```方法：

```java
ObjectMockContext copiedCtx = GlobalObjectMockContext.copy();
```

拷贝会将原context中注册的所有```ObjectGenerator```拷贝一份到新的context中，在新context中修改对象生成器不会影响到原context。

拷贝不是必须的，只有你的测试代码需要与```ObjectMockContext```不一样的配置（比如新注册一个```ObjectGenerator```）时，才是必要的。

现在我们有了一份```GlobalObjectMockContext```的拷贝，可以进行修改自定义了，看一下下面的测试代码：

```java
  @Test
  public void extendRegisteredGenerator() throws NoSuchFieldException {
    ObjectMockContext globalContext = GlobalObjectMockContext.getInstance();

    TestClass ins = context.generate(TestClass.class, basicGenerator ->
      ObjectGenerator.builder(TestClass.class)
          .field("stringField", () -> "Tom")
          .extend(basicGenerator)
    );
    Assert.assertEquals("Tom", ins.getStringField());
  }
```

这个示例代码里，我在调用```generate```方法生成对象时，临时修改了```TestClass```的对象生成器的行为，让它在处理```stringField```字段时只用```"Tom"```这个值。

是的，又是lambda表达式（I like it!），这个lambda表达式是```org.luncert.objectmocker.core.ObjectGeneratorExtender```接口的实现，看一下接口定义：

```java
@FunctionalInterface
public interface ObjectGeneratorExtender {

  /**
   * Extend basic ObjectGenerator.
   * @param basicGenerator basic ObjectGenerator, should not be modified in this call
   * @return new ObjectGenerator
   * @throws Exception java.lang.Exception
   */
  ObjectGenerator extendObjectGenerator(final ObjectGenerator basicGenerator) throws Exception;
}
```

```extendObjectGenerator```接收一个```ObjectGenerator```作为入参，应该返回一个从```basicGenerator```继承来的、新的```ObjectGenerator```，**这是我对使用者的期望，我觉得在这里不应该去修改```basicGenerator```，这很重要！**

再回头看一下那个lambda表达式：

```java
basicGenerator ->
      ObjectGenerator.builder(TestClass.class)
          .field("stringField", () -> "Tom")
          .extend(basicGenerator)
```

它使用了```ObjectGenerator.builder```来创建一个新的、临时的对象生成器，第2、3行很普通，关键在于第4行调用```extend```方法，代替了以前我们用的```build```方法。```extend```包含了```build```的功能：返回```ObjectGenerator```实例，除此之外，它对入参```basicGenerator```进行了一次继承，即拷贝```basicGenerator```中的```ignores```和```fieldGenerator```，并检查```basicGenerator```要生成的类和正在构建的```ObjectGenerator```要生成的类是否一致。**注意：继承不会覆盖第3行的配置，继承指挥添加新的对象生成器没有的属性**。

继承只是临时发挥作用对吧！那没必要拷贝```ObjectMockContext```呀？当然有必要了，接下来我们永久**修改**```ObjectGenerator```的配置：

```java
  @Test
  public void copyContextAndModifyGenerator() throws Exception {
    ObjectMockContext globalContext = GlobalObjectMockContext.getInstance();

    ObjectMockContext copiedCtx = context.copy();
    copiedCtx.modifyObjectGenerator(TestClass.class, generator ->
        generator.addIgnores("shouldBeIgnored"));
    
    TestClass ins = context.generate(TestClass.class);
    Assert.assertNull(ins.getShouldBeIgnored());
  }

```

又又是lambda表达式（I like it!），这个lambda表达式实现了```org.luncert.objectmocker.core.ObjectGeneratorModifer```接口：

```java
@FunctionalInterface
public interface ObjectGeneratorModifier {

  /**
   * modify ObjectGenerator
   * @param generator ObjectGenerator
   * @throws Exception java.lang.Exception
   */
  void accept(ObjectGenerator generator) throws Exception;
}
```

```accept```方法接收一个```ObjectGenerator```，然后你就可以在方法体对对象生成器进行修改了，实际上比```ObjectGeneratorExtender```还要简单些😂