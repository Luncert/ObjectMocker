package org.luncert.objectmocker.core;

import java.io.IOException;
import java.util.Map;

public interface ObjectMockContext {

  /**
   * Register ObjectGenerator.
   * @param objectGenerator build with
   * {@link org.luncert.objectmocker.core.ObjectGenerator.ObjectGeneratorBuilder}
   */
  void register(ObjectGenerator objectGenerator);

  /**
   * Check if any generator has been registered for target class.
   * @param clazz target class.
   * @return boolean
   */
  boolean hasGeneratorFor(Class<?> clazz);

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
  <T> T generate(Class<T> clazz, ObjectGeneratorExtender extender,
                        String...tmpIgnores);

  /**
   * Generate enum, list, string and other basic type with user provided generator.
   * @param supplier lambda implementation of ObjectSupplier
   * @return generated object
   */
  <T> T generate(ObjectSupplier<T> supplier);

  /**
   * Generate enum, list, string and other basic type with responsive generator.
   * e.g. {@code context.generate(ListGenerator.withLength(String.class, 10))}
   * @param clazz mandatory if generator has DynamicTypeGenerator annotation,
   *             otherwise it is optional
   * @param generator any implementation of {@link AbstractGenerator}
   * @return generated object
   */
  <T> T generate(Class<?> clazz, AbstractGenerator<T> generator);

  <T> T generate(Class<T> clazz, Map<String, Object> baseData) throws IOException;

  /**
   * Get target class' ObjectGenerator to change generating strategy.
   * @param clazz target class
   * @param modifier ObjectGeneratorModifier
   */
  void modifyObjectGenerator(Class<?> clazz, ObjectGeneratorModifier modifier)
      throws Exception;

  /**
   * Create a new instance from this context.
   * All generator relating information will be copy into new instance.
   * @return ObjectMockContext new instance.
   */
  ObjectMockContext copy();

  /**
   * Create and return a VirtualObjectMockContext,
   * all modification on VirtualObjectMockContext won't affect the real RealObjectMockContext.
   * @return VirtualObjectMockContext, an extension class of RealObjectMockContext.
   */
  ObjectMockContext createVirtualContext();
}
