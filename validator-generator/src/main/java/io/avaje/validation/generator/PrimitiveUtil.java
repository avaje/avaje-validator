package io.avaje.validation.generator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class PrimitiveUtil {
  private PrimitiveUtil() {}

  private static final Set<String> primitiveValidationTypes = Set.of("int", "long");
  private static final Set<String> primitiveValidationAnnotations =
      new HashSet<>(
          Set.of(
              "AssertFalse",
              "AssertTrue",
              "Range",
              "Min",
              "Max",
              "Positive",
              "PositiveOrZero",
              "Negative",
              "NegativeOrZero"));
  private static final Map<String, String> wrapperMap =
      Map.of(
          "char",
          "Character",
          "byte",
          "Byte",
          "int",
          "Integer",
          "long",
          "Long",
          "short",
          "Short",
          "double",
          "Double",
          "float",
          "Float",
          "boolean",
          "Boolean");

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

  static boolean addPrimitiveValidationAnnotation(String annotationShortName) {
    return primitiveValidationAnnotations.add(annotationShortName);
  }

  static String defaultValue(String shortType) {
    return "boolean".equals(shortType) ? "false" : "0";
  }
}
