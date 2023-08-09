package io.avaje.validation.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class PrimitiveUtil {

  private static final Set<String> primitiveValidationTypes = Set.of("int", "long");
  private static final Set<String> primitiveValidationAnnotations =
    Set.of("Range", "Min", "Max", "Positive", "PositiveOrZero", "Negative", "NegativeOrZero");
  private static final Map<String, String> wrapperMap = new HashMap<>();

  static {
    wrapperMap.put("char", "Character");
    wrapperMap.put("byte", "Byte");
    wrapperMap.put("int", "Integer");
    wrapperMap.put("long", "Long");
    wrapperMap.put("short", "Short");
    wrapperMap.put("double", "Double");
    wrapperMap.put("float", "Float");
    wrapperMap.put("boolean", "Boolean");
  }

  static String wrap(String shortName) {
    final String wrapped = wrapperMap.get(shortName);
    return wrapped != null ? wrapped : shortName;
  }

  static boolean isPrimitive(String typeShortName) {
    return wrapperMap.containsKey(typeShortName);
  }

  static boolean isPrimitiveValidationType(String typeShortName) {
    return primitiveValidationTypes.contains(typeShortName);
  }

  static boolean isPrimitiveValidationAnnotations(String annotationShortName) {
    return primitiveValidationAnnotations.contains(annotationShortName);
  }

  static String defaultValue(String shortType) {
    return "boolean".equals(shortType) ? "false" : "0";
  }
}
