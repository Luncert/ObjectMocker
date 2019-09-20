package org.luncert.objectmocker;

import com.google.common.collect.ImmutableMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassPathUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.luncert.objectmocker.exception.BuildGenerateConfigException;
import org.luncert.objectmocker.exception.GeneratorException;
import org.luncert.objectmocker.exception.InvalidConfigurationException;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public final class ObjectGenerator {

  private ObjectGenerator() {
  }

  private static final String CONFIG_DIR = "generating-config";

  private static final Map<Class, Function<FieldConfig, ?>> GENERATORS = ImmutableMap
      .<Class, Function<FieldConfig, ?>>builder()
      .put(String.class, ObjectGenerator::genString)
      .put(UUID.class, ObjectGenerator::genUUID)
      .put(ZonedDateTime.class, ObjectGenerator::genZonedDateTime)
      .put(Date.class, ObjectGenerator::genDate)
      .put(Boolean.class, ObjectGenerator::genBoolean)
      .put(boolean.class, ObjectGenerator::genBoolean)
      .put(Integer.class, ObjectGenerator::genInteger)
      .put(int.class, ObjectGenerator::genInteger)
      .put(Long.class, ObjectGenerator::genLong)
      .put(long.class, ObjectGenerator::genLong)
      .put(Double.class, ObjectGenerator::genDouble)
      .put(double.class, ObjectGenerator::genDouble)
      .put(BigDecimal.class, ObjectGenerator::genBigDecimal)
      .build();

  /**
   * CustomizedGeneratorInstanceCache cache all customized generators' instance, avoid to create
   * multi instance of the same generator.
   */
  private static Map<Class<Generator>, Generator> customizedGeneratorInstanceCache = new HashMap<>();

  /**
   * CustomizedGenerators uses to get generator efficiently with TypeSignature, when we need to
   * generate an instance again for a class, we don't need to use Class.forName to load AbstractGenerator,
   * we could get appropriate generator with the field's TypeSignature directly.
   */
  private static Map<TypeSignature, Generator> customizedGenerators = new HashMap<>();

  private static final String CONFIG_NAME_GENERATOR = "$generator";
  private static final String CONFIG_NAME_LIST_ELEM = "$listElem";
  private static final String CONFIG_RELATED_CLASS_PATH = "?";

  /**
   * Cache configurations, content: className -> configMap
   */
  private static Map<String, Map> configCache = new HashMap<>();

  public static <T> T generate(Class<T> targetClass, String... firstIgnores) {
    return generate(targetClass, null, firstIgnores);
  }

  /**
   * <p>Generate fields' value for target object. Default supported type:</p>
   * <ul>
   *   <li>String: {@link ObjectGenerator#genString}</li>
   *   <li>UUID: {@link ObjectGenerator#genUUID}</li>
   *   <li>ZonedDateTimeGenerator: {@link ObjectGenerator#genZonedDateTime}</li>
   *   <li>Integer: {@link ObjectGenerator#genInteger}</li>
   *   <li>Long: {@link ObjectGenerator#genLong}</li>
   *   <li>Double: {@link ObjectGenerator#genDouble}</li>
   *   <li>BigDecimal: {@link ObjectGenerator#genBigDecimal}</li>
   * </ul>
   * <p>AbstractGenerator will read and cache configuration from a file with path <code>classpath:{className}.yml</code>.</p>
   *
   * @param targetClass target object
   * @param firstConfigs customize field generating configurations, which will overwrite the
   * configurations read from config file
   * @param firstIgnores declare which properties should be ignored
   * @return T target object
   */
  @SuppressWarnings("unchecked")
  public static <T> T generate(Class<T> targetClass,
                               Map<String, Map<String, Object>> firstConfigs,
                               String... firstIgnores) {
    Objects.requireNonNull(targetClass, "Parameter targetClass must be non-null");

    if (GENERATORS.containsKey(targetClass)) {
      throw new GeneratorException("targetClass must be customized type.");
    }

    // try create new instance for target class

    Object target;
    try {
      target = targetClass.newInstance();
    } catch (Exception e) {
      throw new GeneratorException("Failed to create a new instance of target class " +
          targetClass.getSimpleName() + ".");
    }

    String className = targetClass.getSimpleName();

    //  read configuration

    loadConfig(className);

    Map fieldsConfig = getFieldsConfig(className);
    Set ignoresConfig = getIgnoresConfig(className);

    if (ignoresConfig.isEmpty()) {
      ignoresConfig = new HashSet(Arrays.asList(firstIgnores));
    } else {
      ignoresConfig.addAll(Arrays.asList(firstIgnores));
    }

    if (Objects.isNull(firstConfigs)) {
      firstConfigs = Collections.EMPTY_MAP;
    }

    // generate object

    Class<?> objectClass = targetClass;
    while (!Object.class.equals(objectClass)) {
      try {
        for (Field field : objectClass.getDeclaredFields()) {
          String fieldName = field.getName();

          // skip static or final field
          int modifiers = field.getModifiers();
          if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
            log.debug("{}.{} - Field has been skipped because it has static or final modifier.",
                className, fieldName);
            continue;
          }

          // skip field need be ignored
          if (ignoresConfig.contains(fieldName)) {
            continue;
          }

          // set field accessible
          field.setAccessible(true);

          // if target field need an enum value, generate value directly
          Class<?> fieldType = field.getType();
          if (fieldType.isEnum()) {
            Object[] enumValues = fieldType.getEnumConstants();
            // cannot generate value with empty enum
            if (enumValues.length == 0) {
              throw new GeneratorException("Couldn't generate a value for field " +
                  fieldName + "using empty enum" + fieldType.getSimpleName() + ".");
            }
            field.set(target, enumValues[RandomUtils.nextInt(0, enumValues.length)]);
            continue;
          }

          // get field configuration
          Object objFieldConfig = fieldsConfig.get(fieldName);
          Map fieldConfig = (objFieldConfig instanceof Map) ?
              (Map) objFieldConfig : Collections.EMPTY_MAP;

          // merge first configuration
          Map<String, Object> firstConfig = firstConfigs.get(fieldName);
          if (Objects.nonNull(firstConfig)) {
            if (fieldConfig.isEmpty()) {
              fieldConfig = firstConfig;
            } else {
              fieldConfig.putAll(firstConfig);
            }
          }

          field.set(target, generate(className, field, fieldConfig, objectClass));
        }
      } catch (IllegalAccessException e) {
        throw new GeneratorException(
            "Failed to set field value of object with class " + className + ".", e);
      }

      // turn to super class
      objectClass = objectClass.getSuperclass();
    }
    return targetClass.cast(target);
  }

  private static Object generate(String baseClassName, Field field,
                                 Map<String, Object> fieldConfig, Class<?> objectClass) {
    try {
      // use customized generator or standard generator
      Object generatorName = fieldConfig.get(CONFIG_NAME_GENERATOR);
      if (generatorName instanceof String) {
        return generateWithCustomizedGenerator(field, fieldConfig, objectClass,
            (String) generatorName);
      } else if (List.class.equals(field.getType())) {
        return generateListField(field, fieldConfig, objectClass, baseClassName);
      } else {
        return generateWithStandardGenerator(field, fieldConfig, baseClassName);
      }
    } catch (ClassNotFoundException e) {
      throw new GeneratorException("Couldn't find customized generator.", e);
    } catch (InstantiationException e) {
      throw new GeneratorException("Failed to instantiate generator.", e);
    } catch (IllegalAccessException e) {
      throw new GeneratorException(
          "Failed to set field value of object with class " + baseClassName + ".", e);
    }
  }

  private static Object generateWithCustomizedGenerator(Field field,
                                                        Map<String, Object> fieldConfig,
                                                        Class<?> objectClass, String generatorName)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    // replace CONFIG_RELATED_CLASS_PATH with this class's package name
    if (generatorName.startsWith(CONFIG_RELATED_CLASS_PATH)) {
      String packageName = ObjectGenerator.class.getPackage().getName();
      generatorName = generatorName.replace(CONFIG_RELATED_CLASS_PATH, packageName);
    }
    Class<?> generatorClass = Class.forName(generatorName);
    if (!Generator.class.isAssignableFrom(generatorClass)) {
      throw new GeneratorException("Customized generator " + generatorClass.getSimpleName() +
          " must implement AbstractGenerator interface.");
    }

    // get a cached or new instance of specified generator
    Generator generator = getCustomizedGenerator(
        new TypeSignature(field.getName(), objectClass),
        (Class<Generator>) generatorClass);

    return generator.generate(fieldConfig);
  }

  private static Object generateListField(Field field, Map<String, Object> fieldConfig,
                                          Class<?> objectClass, String baseClassName)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    // len: list size, default 3

    String fieldName = field.getName();

    // determine element type
    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
    Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
    if (actualTypeArgs.length == 0) {
      throw new GeneratorException("Failed to determine parameterized type of list type field "
          + fieldName + " for class " + baseClassName + ".");
    }
    Class elemClass = (Class) actualTypeArgs[0];

    // read list config
    FieldConfig config = FieldConfig.of(fieldConfig);
    int len = config.get("len", Integer::valueOf, 3);

    // generate list
    Object tmp = config.get(CONFIG_NAME_LIST_ELEM);
    FieldConfig elemConfig;
    if (Objects.isNull(tmp)) {
      elemConfig = FieldConfig.of(Collections.EMPTY_MAP);
    } else if (tmp instanceof Map) {
      elemConfig = FieldConfig.of((Map) tmp);
    } else {
      throw new GeneratorException(baseClassName + "." + fieldName
          + "elemConfig of must be a map, not " + tmp.getClass());
    }

    // generate list
    List list = new ArrayList<>();
    if (elemConfig.containsKey(CONFIG_NAME_GENERATOR)) {
      for (int i = 0; i < len; i++) {
        list.add(generateWithCustomizedGenerator(field, elemConfig.config,
            objectClass, elemConfig.get(CONFIG_NAME_GENERATOR, "")));
      }
    } else if (GENERATORS.containsKey(elemClass)) {
      Function<FieldConfig, ?> generator = GENERATORS.get(elemClass);
      for (int i = 0; i < len; i++) {
        list.add(generator.apply(elemConfig));
      }
    } else {
      if (!elemConfig.isEmpty()) {
        log.warn("{}.{} - $listElem option only works on non-customized type.", baseClassName,
            fieldName);
      }
      for (int i = 0; i < len; i++) {
        list.add(ObjectGenerator.generate(elemClass));
      }
    }
    return list;
  }

  private static Object generateWithStandardGenerator(Field field, Map<String, Object> fieldConfig,
                                                      String baseClassName) {
    Function<FieldConfig, ?> generator = GENERATORS.get(field.getType());
    if (Objects.nonNull(generator)) {
      return generator.apply(FieldConfig.of(fieldConfig));
    } else {
      throw new GeneratorException(baseClassName + "." + field.getName()
          + " - Couldn't find any appropriate generator for type "
          + field.getType() + ".");
    }
  }

  private static void loadConfig(String className) {
    String configPath = CONFIG_DIR + "/" + className + ".yml";

    try {
      Map config = new Yaml().load(new FileInputStream(configPath));
      configCache.put(className, config == null ? Collections.EMPTY_MAP : config);
    } catch (FileNotFoundException e) {
      log.debug("Could not find configuration file for class " + className + ".");
    }
  }

  private static Map getFieldsConfig(String className) throws InvalidConfigurationException {
    Map config = configCache.get(className);
    if (config != null) {
      Object obj = config.get("fields");
      if (obj != null) {
        if (obj instanceof Map) {
          return (Map) obj;
        } else {
          throw new InvalidConfigurationException("Element 'fields' must be a map.");
        }
      }
    }
    return Collections.EMPTY_MAP;
  }

  @SuppressWarnings("unchecked")
  private static Set getIgnoresConfig(String className) throws InvalidConfigurationException {
    Map config = configCache.get(className);
    if (config != null) {
      Object obj = config.get("ignores");
      if (obj != null) {
        if (obj instanceof List) {
          return new HashSet((List) obj);
        } else {
          throw new InvalidConfigurationException("Element 'ignores' must be a list.");
        }
      }
    }
    return Collections.EMPTY_SET;
  }

  private static class TypeSignature {

    String fieldName;
    Class baseClass;

    TypeSignature(String fieldName, Class baseClass) {
      this.fieldName = fieldName;
      this.baseClass = baseClass;
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof TypeSignature)) {
        return false;
      }
      TypeSignature t = (TypeSignature) object;
      return !ObjectUtils.notEqual(fieldName, t.fieldName)
          && !ObjectUtils.notEqual(baseClass, t.baseClass);
    }

    @Override
    public int hashCode() {
      return fieldName.hashCode() ^ baseClass.hashCode();
    }
  }

  /**
   * Get customized generator,
   *
   * @param typeSignature TypeSignature
   * @param generatorClass Customized generator's class
   * @return AbstractGenerator instance
   * @throws IllegalAccessException failed to instantiate generator
   * @throws InstantiationException failed to instantiate generator
   */
  private static Generator getCustomizedGenerator(TypeSignature typeSignature,
                                                  Class<Generator> generatorClass) throws IllegalAccessException, InstantiationException {
    // try to get the instance bound to this typeSignature
    Generator generator = customizedGenerators.get(typeSignature);
    if (generator == null) {
      // try to get the instance from instanceCache
      generator = customizedGeneratorInstanceCache.get(generatorClass);
      if (generator == null) {
        // create a new instance
        generator = generatorClass.newInstance();
        customizedGeneratorInstanceCache.put(generatorClass, generator);
      }
      customizedGenerators.put(typeSignature, generator);
    }
    return generator;
  }

  private static class FieldConfig {

    Map config;

    FieldConfig(Map config) {
      this.config = config;
    }

    static FieldConfig of(Map config) {
      return new FieldConfig(config);
    }

    <T> T get(String name, T defaultValue) {
      Object tmp = config.get(name);
      try {
        return tmp != null ? (T) tmp : defaultValue;
      } catch (NumberFormatException e) {
        throw new RuntimeException("Failed to parse config " + name + ".", e);
      }
    }

    <T> T get(String name, Function<String, T> parser, T defaultValue) {
      Object tmp = config.get(name);
      try {
        return tmp != null ?
            parser.apply(tmp.toString()) : defaultValue;
      } catch (NumberFormatException e) {
        throw new RuntimeException("Failed to parse config " + name + ".", e);
      }
    }

    Object get(String name) {
      return config.get(name);
    }

    boolean containsKey(String name) {
      return config.containsKey(name);
    }

    boolean isEmpty() {
      return config.isEmpty();
    }

  }

  // general generators

  /**
   * <ul>
   * Options:
   * <li>len: string length, default 8</li>
   * </ul>
   */
  private static String genString(FieldConfig config) {
    int len = config.get("len", Integer::valueOf, 8);
    return RandomStringUtils.randomAlphabetic(len);
  }

  private static UUID genUUID(FieldConfig config) {
    return UUID.randomUUID();
  }

  private static ZonedDateTime genZonedDateTime(FieldConfig config) {
    return ZonedDateTime.now();
  }

  private static Date genDate(FieldConfig config) {
    return new Date();
  }

  /**
   * <ul>
   * Options:
   * </ul>
   */
  private static Boolean genBoolean(FieldConfig config) {
    return RandomUtils.nextBoolean();
  }

  /**
   * <ul>
   * Options:
   * <li>start: start from, default 0</li>
   * <li>end: end from, default 9</li>
   * </ul>
   */
  private static Integer genInteger(FieldConfig config) {
    int start = config.get("start", Integer::valueOf, 0);
    int end = config.get("end", Integer::valueOf, 9);
    return RandomUtils.nextInt(start, end);
  }

  /**
   * <ul>
   * Options:
   * <li>start: start from, default 0</li>
   * <li>end: end from, default 9</li>
   * </ul>
   */
  private static Long genLong(FieldConfig config) {
    int start = config.get("start", Integer::valueOf, 0);
    int end = config.get("end", Integer::valueOf, 9);
    return RandomUtils.nextLong(start, end);
  }

  /**
   * <ul>
   * Options:
   * <li>start: start from, default 0</li>
   * <li>end: end from, default 1</li>
   * </ul>
   */
  private static Double genDouble(FieldConfig config) {
    double start = config.get("start", Double::valueOf, 0D);
    double end = config.get("end", Double::valueOf, 1D);
    return RandomUtils.nextDouble(start, end);
  }

  /**
   * <ul>
   * Options:
   * <li>start: start from, default 0</li>
   * <li>end: end from, default 1</li>
   * </ul>
   */
  private static BigDecimal genBigDecimal(FieldConfig config) {
    return new BigDecimal(genDouble(config));
  }

  public static class GeneratorConfigBuilder {

    private Map<String, Map<String, Object>> config = new HashMap<>();

    private String selectedField;

    private GeneratorConfigBuilder() {
    }

    public static GeneratorConfigBuilder create() {
      return new GeneratorConfigBuilder();
    }

    private void checkIfFieldSelected() {
      if (selectedField == null) {
        throw new BuildGenerateConfigException(
            "No field selected, call selectField to select a field.");
      }
    }

    public GeneratorConfigBuilder selectField(String fieldName) {
      selectedField = fieldName;
      if (!config.containsKey(fieldName)) {
        config.put(fieldName, new HashMap<>());
      }
      return this;
    }

    public GeneratorConfigBuilder put(String configName, String configValue) {
      putConfig(configName, configValue);
      return this;
    }

    public GeneratorConfigBuilder put(String configName, Boolean configValue) {
      putConfig(configName, configValue);
      return this;
    }

    public GeneratorConfigBuilder put(String configName, Integer configValue) {
      putConfig(configName, configValue);
      return this;
    }

    public GeneratorConfigBuilder put(String configName, Double configValue) {
      putConfig(configName, configValue);
      return this;
    }

    private void putConfig(String configName, Object configValue) {
      checkIfFieldSelected();
      config.get(selectedField).put(configName, configValue);
    }

    public Map<String, Map<String, Object>> build() {
      return config;
    }

  }

}
