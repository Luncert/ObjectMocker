package org.luncert.objectmocker.core;

import org.luncert.objectmocker.builtinGenerator.*;

/**
 * Builtin Generator Builder
 */
public final class BuiltinGeneratorBuilder {

  private BuiltinGeneratorBuilder() {}

  public static BigDecimalGenerator GBigDecimal(Double defaultValue) {
    return BigDecimalGenerator.defaultValue(defaultValue);
  }

  public static BigDecimalGenerator GBigDecimal(Double start, Double end) {
    return BigDecimalGenerator.rangeFrom(start, end);
  }

  public static BooleanGenerator GBoolean() {
    return BooleanGenerator.singleton();
  }

  public static DateGenerator GDate() {
    return DateGenerator.singleton();
  }

  public static DoubleGenerator GDouble(Double defaultValue) {
    return DoubleGenerator.defaultValue(defaultValue);
  }

  public static DoubleGenerator GDouble(Double start, Double end) {
    return DoubleGenerator.rangeFrom(start, end);
  }

  public static <T> EnumGenerator GEnum(T defaultValue) {
    return EnumGenerator.defaultValue(defaultValue);
  }

  public static <T> EnumGenerator GEnum(T[] rangeValue) {
    return EnumGenerator.rangeFrom(rangeValue);
  }

  public static IntegerGenerator GInteger(int defaultValue) {
    return IntegerGenerator.defaultValue(defaultValue);
  }

  public static IntegerGenerator GInteger(int start, int end) {
    return IntegerGenerator.rangeFrom(start, end);
  }

  public static LongGenerator GLong(long defaultValue) {
    return LongGenerator.defaultValue(defaultValue);
  }

  public static LongGenerator GLong(long start, long end) {
    return LongGenerator.rangeFrom(start, end);
  }

  public static StringGenerator GString(String ...rangeValue) {
    return StringGenerator.rangeFrom(rangeValue);
  }

  public static StringGenerator GString(int len) {
    return StringGenerator.withLength(len);
  }

  public static UuidGenerator GUuid() {
    return UuidGenerator.singleton();
  }

  public static ZonedDateTimeGenerator GZonedDateTime() {
    return ZonedDateTimeGenerator.singleton();
  }
}
