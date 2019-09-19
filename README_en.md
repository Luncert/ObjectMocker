# ObjectMocker

![](https://img.shields.io/badge/language-java-brightgreen.svg?style=plastic)![](https://img.shields.io/badge/build-passing-brightgreen)![](https://img.shields.io/badge/coverage-80%25-brightgreen)

## Usage

The section will introduce you the simplest usage of ObjectMocker.

### Step1. Build an ```ObjectGenerator``` for Target Class

At first, we create a simple class for test:

```java
@Data
public class Person {
    private String name;
    private String uuid;
    private List<String> phoneList;
    private String deprecatedField;
}
```

Then create an ```ObjectGenerator``` for Person class using ```ObjectGeneratorBuilder```:

```java
ObjectGenerator personGenerator = ObjectGenerator.builder(Person.class)
    .addIgnores("deprecatedField")
    .setGenerator("uuid", (ctx, cls) -> UUID.randomUUID().toString())
    .setGenerator("phoneList", ListGenerator.withLength(5))
    .build();
```

You should notice that:

* I invoke ```addIgnores``` to ask ```personGenerator``` to ignore ```deprecatedField``` when generates Person class.
* I provide a lambda expression to ```personGenerator``` to generate the specified field ```uuid```. The two parameters will be explained in following part.

And you may not realize that the ```name``` field of Person class will be generated with built-in ```StringGenerator```. Actually, there are a lot of built-in generators to handle most of the normal java types, for example: ```ListGenerator, EnumGenerator, IntegerGenerator``` and etc, you could find them in package ```util.generatorx.builtinGenerator```.

Now we have created the ```personGenerator```, it's time to register it to a ```ObjectMockContext``` and use it.

### Step2. Create a ```ObjectMockContext``` and Register Our Generator

```java
ObjectMockContext ctx = ObjectMocker.context()
    .register(personGenerator)
    .create();
Person person = ctx.generate(Person.class);
```

## Advanced Practice

### I. Configurable Built-in Generators

#### 1. ```BigDecimalGenerator```

* ```public static BigDecimalGenerator defaultValue(Double defaultValue)```: Created ```BigDecimalGenerator``` only outputs the provided ```defaultValue```.
* ```public static BigDecimalGenerator rangeFrom(Double start, Double end)```: Created ```BigDecimalGenerator``` will generate a value randomly range from ```start``` to ```end```.

#### 2. ```BooleanGenerator```

No creator method, create it by default constructor.

#### 3. ```DateGenerator```

No creator method, create it by default constructor.

#### 4. ```DoubleGenerator```

* ```public static DoubleGenerator defaultValue(Double defaultValue)```: Created ```DoubleGenerator``` only outputs the provided ```defaultValue```.
* ```public static DoubleGenerator rangeFrom(Double start, Double end)```: Created ```DoubleGenerator``` will generate a value randomly range from ```start``` to ```end```.

#### 5. ```EnumGenerator```

* ```public static <T> EnumGenerator defaultValue(T defaultValue)```: Created ```EnumGenerator``` only outputs the provided ```defaultValue```.
* ```public static <T> EnumGenerator rangeFrom(T[] rangeValue)```: Created ```EnumGenerator``` will generate a value randomly from the giving array.

#### 6. ```IntegerGenreator```

* ```public static IntegerGenerator defaultValue(int defaultValue)```: Created ```IntegerGenreator``` only outputs the provided ```defaultValue```.
* ```public static IntegerGenerator rangeFrom(int start, int end)```: Created ```IntegerGenreator``` will generate a value randomly range from ```start``` to ```end```.

#### 7. ```ListGenerator```

* ```public static ListGenerator withLength(int len)```: Created ```ListGenerator``` will generate a list of length ```len```.
* ```public static ListGenerator withElementGenerator(int len, ObjectSupplier<Object> elementGenerator)```: This creator method enhances the above one, created ```ListGenerator``` will use the provided parameter ```elementGenerator``` to generate the list elements. By default, ```ListGenerator``` asks ```ObjectMockContext``` to generate the list elements.

#### 8. ```LongGenerator```

* ```public static LongGenerator defaultValue(Long defaultValue)```: Created ```LongGenerator ``` only outputs the provided ```defaultValue```.
* ```public static LongGenerator rangeFrom(Long start, Long end)```: Created ```LongGenerator ``` will generate a value randomly range from ```start``` to ```end```.

#### 9. ```StringGenerator```

* ```public static StringGenerator defaultValue(String defaultValue)```: Created ```StringGenerator ``` only outputs the provided ```defaultValue```.
* ```public static StringGenerator rangeFrom(String...rangeValue)```: Created ```StringGenerator ``` will generate a value randomly from the giving string array.
* ```public static StringGenerator withLength(int len)```: Created ```StringGenerator``` will generate a stringof length ```len```.

#### 10. ```UUIDGenerator```

No creator method, create it by default constructor.

#### 11. ```ZonedDateTimerGenerator```

No creator method, create it by default constructor.

### II. Provide Customized Generator

There are two ways to provide a  customized generator:

Create a class and extend abstract class ```AbstractGenerator:```

```java
public class StringUuidGenerator extends AbstractGenerator<String> {

  // It's mandatory to create a constructor and invoke super here.
  private StringUuidGenerator(ObjectSupplier<String> supplier) {
    super(supplier);
  }

  public StringUuidGenerator() {
    this((ctx, cls) -> UUID.randomUUID().toString());
  }
    
  public static StringUuidGenerator limit16() {
    return new StringUuidGenerator((ctx, cls) ->
        UUID.randomUUID().toString().substring(0, 16));
  }
}
```

Or use lambda expression directly when build ```ObjectGenerator```:

```java
ObjectGenerator.builder(XXX.class)
    .setGenerator("stringUuid", (ctx, cls) -> UUID.randomUUID().toString())
    .build();
```

In fact, the lambda expression will be compiled to a anonymous class implementing function interface ```ObjectSupplier```, and then, ```ObjectGeneratorBuilder#setGenerator``` will wrap this anonymous class with another anonymous class:

```java
// source code of ObjectGenerator
public ObjectGeneratorBuilder setGenerator(String fieldName, ObjectSupplier supplier) throws NoSuchFieldException {
  objectGeneratorInstance.setGenerator(fieldName, new AbstractGenerator(supplier) {});
  return this;
}
```

About the two parameters ```ctx``` and ```cls```:

* Each ```ObjectGenerator``` and related implementation class of ```AbstractGenerator``` will hold the reference to a ```ObjectMockContext``` instance where the ```ObjectGenerator``` registers itself, and ```ctx``` is the reference holding by the ```AbstractGenerator```.
* ```cls``` is the target class to be generated.

### III. Extend Registered Generator

For example, you have built an ```ObjectGenerator``` and provided some customized or configured Generator for the target class' field like this:

```java
ObjectMockContext ctx = ObjectMocker.context()
    .register(ObjectGenerator.builder(Person.class)
        .setGenerator("uuid", new StringUuidGenerator())
        .build())
    .create();
```

Now you want to use the context to generate an instance of Person class with an incremental number as value of  field ```uuid```.

```java
int id = 0;
Person person = ctx.generate(Person.class,
    (basicGenerator) ->
        ObjectGenerator.builder(Person.class)
            .setGenerator("uuid", (ctx, cls) -> String.valueOf(id++))
            .extend(basicGenerator)
            .build()
    );
```

The key is invoking ```ObjectGenertorBuilder#extend``` method, it will copy the ignores, user provided field generators from the basic ```ObjectGenerator``` and check whether the target class is matched. It's sure that the inherited generator's content won't be overwrite.

## Demo

No demo.