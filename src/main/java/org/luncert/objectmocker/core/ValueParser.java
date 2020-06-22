package org.luncert.objectmocker.core;

/**
 * Function interface using to parse text value into different type.
 */
@FunctionalInterface
interface ValueParser {
  
  Object parse(String raw);
}
