package org.luncert.objectmocker.core;

@FunctionalInterface
public interface ObjectGeneratorModifier {

  void accept(ObjectGenerator generator) throws Exception;
}
