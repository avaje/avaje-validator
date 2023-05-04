package io.avaje.validation.generator;

import java.util.HashMap;
import java.util.Map;

final class GenericTypeMap {

  private static final Map<String, String> basic = new HashMap<>();

  private GenericTypeMap() {}

  static {
    basic.put("char", "Character.TYPE");
    basic.put("byte", "Byte.TYPE");
    basic.put("boolean", "Boolean.TYPE");
    basic.put("int", "Integer.TYPE");
    basic.put("long", "Long.TYPE");
    basic.put("short", "Short.TYPE");
    basic.put("double", "Double.TYPE");
    basic.put("float", "Float.TYPE");

    basic.put("java.lang.Boolean", "Boolean.class");
    basic.put("java.lang.Integer", "Integer.class");
    basic.put("java.lang.Long", "Long.class");
    basic.put("java.lang.Short", "Short.class");
    basic.put("java.lang.Double", "Double.class");
    basic.put("java.lang.Float", "Float.class");
    basic.put("java.lang.String", "String.class");
  }

  static String typeOfRaw(String rawType) {
    return basic.get(rawType);
  }
}
