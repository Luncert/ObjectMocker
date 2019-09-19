package org.luncert.objectmocker.annotation;

import java.lang.annotation.*;

/**
 * Declare target generator is used for generate dynamic type, e.g. {@code List<String>;}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DynamicTypeGenerator {
}
