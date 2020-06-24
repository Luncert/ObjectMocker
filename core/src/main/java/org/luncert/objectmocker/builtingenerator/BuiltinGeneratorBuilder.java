package org.luncert.objectmocker.builtingenerator;

import org.luncert.objectmocker.core.AbstractGenerator;
import org.luncert.objectmocker.core.ObjectSupplier;

/**
 * Builtin Generator Builder.
 * @author Luncert
 */
public final class BuiltinGeneratorBuilder {

  private BuiltinGeneratorBuilder() {
  }

  public static BigDecimalGenerator bigDecimalGenerator(Double defaultValue) {
    return BigDecimalGenerator.defaultValue(defaultValue);
  }

  public static BigDecimalGenerator bigDecimalGenerator(Double start, Double end) {
    return BigDecimalGenerator.rangeFrom(start, end);
  }

  public static BooleanGenerator booleanGenerator() {
    return BooleanGenerator.singleton();
  }

  public static DateGenerator dateGenerator() {
    return DateGenerator.singleton();
  }

  public static DoubleGenerator doubleGenerator(Double defaultValue) {
    return DoubleGenerator.defaultValue(defaultValue);
  }

  public static DoubleGenerator doubleGenerator(Double start, Double end) {
    return DoubleGenerator.rangeFrom(start, end);
  }

  public static <T> EnumGenerator enumGenerator(T defaultValue) {
    return EnumGenerator.defaultValue(defaultValue);
  }

  public static <T> EnumGenerator enumGenerator(T[] rangeValue) {
    return EnumGenerator.rangeFrom(rangeValue);
  }

  public static IntegerGenerator integerGenerator(int defaultValue) {
    return IntegerGenerator.defaultValue(defaultValue);
  }

  public static IntegerGenerator integerGenerator(int start, int end) {
    return IntegerGenerator.rangeFrom(start, end);
  }

  public static ListGenerator listGenerator(int len) {
    return ListGenerator.withLength(len);
  }

  public static ListGenerator listGenerator(int len, ObjectSupplier<Object> elementGenerator) {
    return ListGenerator.withElementGenerator(len, elementGenerator);
  }

  public static LongGenerator longGenerator(long defaultValue) {
    return LongGenerator.defaultValue(defaultValue);
  }

  public static LongGenerator longGenerator(long start, long end) {
    return LongGenerator.rangeFrom(start, end);
  }

  public static StringGenerator stringGenerator(String...rangeValue) {
    return StringGenerator.rangeFrom(rangeValue);
  }

  public static StringGenerator stringGenerator(int len) {
    return StringGenerator.withLength(len);
  }

  public static UuidGenerator uuidGenerator() {
    return UuidGenerator.singleton();
  }

  public static ZonedDateTimeGenerator zonedDateTimeGenerator() {
    return ZonedDateTimeGenerator.singleton();
  }
}
