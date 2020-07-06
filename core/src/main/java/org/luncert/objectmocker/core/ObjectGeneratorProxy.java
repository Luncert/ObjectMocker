package org.luncert.objectmocker.core;

import org.luncert.objectmocker.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Proxy:
 * <li>add/remove ignoring shouldn't affect client generator</li>
 * <li>remove ignoring should do work</li>
 */
final class ObjectGeneratorProxy<T> extends ObjectGenerator<T> {
  
  private ObjectGenerator<T> client;
  
  private Set<String> includes = new HashSet<>();
  
  ObjectGeneratorProxy(ObjectGenerator<T> client) {
    super(client.getTargetType());
    this.client = client;
  }
  
  @Override
  public void addIgnores(String...fieldNames) {
    for (String fieldName : fieldNames) {
      // add new ignore when client doesn't have the same ignore
      if (!client.hasIgnore(fieldName)) {
        includes.remove(fieldName);
        ignores.add(fieldName);
      }
    }
  }
  
  @Override
  public boolean hasIgnore(String fieldName) {
    return ignores.contains(fieldName)
        || !includes.contains(fieldName) && client.hasIgnore(fieldName);
  }
  
  @Override
  public void removeIgnores(String...fieldNames) {
    for (String fieldName : fieldNames) {
      if (!this.ignores.remove(fieldName)) {
        // if client has the same ignore, we can't change client's data,
        // just add it to includes
        if (client.hasIgnore(fieldName)) {
          includes.add(fieldName);
        }
      }
    }
  }
  
  @Override
  public Set<String> getIgnores() {
    Set<String> tmp = client.getIgnores();
    tmp.removeAll(includes);
    tmp.addAll(ignores);
    return tmp;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  protected Object generateField(Field field) {
    Class<?> fieldType = field.getType();
    // generate field value using fieldGenerator
    AbstractGenerator generator = fieldGenerators.get(field);
    if (generator != null) {
      Class<?> elemType = fieldType;
      // if field is a list, we should forward its parameter type to the generator
      if (List.class.equals(elemType)) {
        elemType = ReflectionUtils.getParameterType(field).get(0);
      }
      return generator.generate(elemType);
    } else {
      // delegate to client
      return client.generateField(field);
    }
  }
}
