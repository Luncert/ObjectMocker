package org.luncert.objectmocker.core;

import java.util.Optional;

public interface ObjectMockContext {

  /**
   * Register ObjectGenerator.
   * @param objectGenerator build with
   * {@link ObjectGeneratorBuilder}
   */
  void register(AbstractObjectGenerator objectGenerator);

  /**
   * Check if any generator has been registered for target class.
   * @param clazz target class.
   * @return boolean
   */
  boolean isConfiguredClass(Class<?> clazz);

  /**
   * Only used to generate customized class (not enum, not interface).
   * @param clazz target object type
   * @param tmpIgnores ignore specified fields
   * @return generated object
   */
  <T> T generate(Class<T> clazz, String... tmpIgnores);

  /**
   * Only used to generate customized class (not enum, not interface).
   * @param clazz target object type, will be used to find the basic ObjectGenerator
   * @param extender to provide the extended new ObjectGenerator
   * @param tmpIgnores ignore specified fields
   * @return generated object
   */
  <T> T generate(Class<T> clazz, ObjectGeneratorExtender<T> extender,
                        String...tmpIgnores);
  
  /**
   * Generate in pre-configured context.
   * e.g. {@code context.generate(StringGenerator.withLength(10))}
   * @param generator any implementation of {@link AbstractGenerator}
   * @return generated object
   */
  <T> T generate(AbstractGenerator<T> generator);

  /**
   * Generate enum, list, string and other basic type with responsive generator.
   * e.g. {@code context.generate(ListGenerator.withLength(String.class, 10))}
   * @param generator any implementation of {@link AbstractGenerator}
   * @param elementType type of element
   * @return generated object
   */
  <T> T generate(AbstractGenerator<T> generator, Class<?> elementType);
  
  /**
   * Get target class' ObjectGenerator.
   */
  <T> Optional<AbstractObjectGenerator<T>> getObjectGenerator(Class<T> targetClazz);

  /**
   * Create and return a child context,
   * all modification on child context won't affect the parent context.
   * @return child context, an extension class of RealObjectMockContext.
   */
  ObjectMockContext createChildContext();
}
