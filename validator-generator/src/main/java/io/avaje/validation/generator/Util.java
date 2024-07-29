package io.avaje.validation.generator;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static io.avaje.validation.generator.APContext.logError;
import static io.avaje.validation.generator.APContext.typeElement;

final class Util {

  private static final String NULLABLE = "Nullable";
  private static final Set<String> BASIC_TYPES = Set.of("java.lang.String", "java.math.BigDecimal");

  private Util() {}

  static boolean isValid(Element e) {
    return AvajeValidPrism.isPresent(e)
      || JavaxValidPrism.isPresent(e)
      || JakartaValidPrism.isPresent(e);
  }

  /** Return true if the element has a Nullable annotation. */
  static boolean isNullable(Element p) {
    if (ProcessorUtils.hasAnnotationWithName(p, NULLABLE)) {
      return true;
    }
    var type =
      p instanceof ExecutableElement ex
        ? UType.parse(ex.getReturnType())
        : UType.parse(p.asType());

    for (final AnnotationMirror mirror : type.annotations()) {
      if (NULLABLE.equalsIgnoreCase(shortName(mirror.getAnnotationType().toString()))) {
        return true;
      }
    }

    return false;
  }

  static boolean validImportType(String type, String packageName) {
    return type.indexOf('.') > -1
      && !type.startsWith("java.lang.")
      && importDifferentPackage(type, packageName)
      || importJavaLangSubpackage(type);
  }

  private static boolean importDifferentPackage(String type, String packageName) {
    return type.replace(packageName + '.', "").indexOf('.') > 0;
  }

  private static boolean importJavaLangSubpackage(String type) {
    return type.startsWith("java.lang.") && importDifferentPackage(type, "java.lang");
  }

  static String shortName(String fullType) {
    final int p = fullType.lastIndexOf('.');
    if (p == -1) {
      return fullType;
    }
    return fullType.substring(p + 1);
  }

  static String initCap(String input) {
    if (input.length() < 2) {
      return input.toUpperCase();
    }
    return Character.toUpperCase(input.charAt(0)) + input.substring(1);
  }

  static String initLower(String name) {
    final StringBuilder sb = new StringBuilder(name.length());
    boolean toLower = true;
    for (final char ch : name.toCharArray()) {
      if (Character.isUpperCase(ch)) {
        if (toLower) {
          sb.append(Character.toLowerCase(ch));
        } else {
          sb.append(ch);
        }
      } else {
        sb.append(ch);
        toLower = false;
      }
    }
    return sb.toString();
  }

  /**
   * Return the base type given the ValidationAdapter type.
   */
  static String baseTypeOfAdapter(String adapterFullName) {
    final var element = typeElement(adapterFullName);
    if (element == null) {
      throw new NullPointerException("Element not found for [" + adapterFullName + "]");
    }
    return baseTypeOfAdapter(element);
  }

  static String baseTypeOfAdapter(TypeElement element) {
    return Optional.of(element.getSuperclass())
      .filter(t ->
        t.toString().contains("io.avaje.validation.adapter.AbstractConstraintAdapter")
          || t.toString().contains("io.avaje.validation.adapter.PrimitiveAdapter"))
      .or(validationAdapter(element))
      .map(UType::parse)
      .map(UType::param0)
      .map(UType::fullWithoutAnnotations)
      .orElseGet(() -> {
        logError(element, "Custom Constraint adapters must extend AbstractConstraintAdapter or implement ValidationAdapter");
        return "Invalid";
      });
  }

  static boolean isPrimitiveAdapter(TypeElement element) {
    return Optional.of(element.getSuperclass())
      .filter(t -> t.toString().contains("io.avaje.validation.adapter.PrimitiveAdapter"))
      .or(primitiveAdapter(element))
      .isPresent();
  }

  private static Supplier<Optional<? extends TypeMirror>> validationAdapter(TypeElement element) {
    return () -> element.getInterfaces().stream()
      .filter(t ->
        t.toString().contains("io.avaje.validation.adapter.ValidationAdapter")
          || t.toString()
          .contains("io.avaje.validation.adapter.ValidationAdapter.Primitive"))
      .findFirst();
  }

  private static Supplier<Optional<? extends TypeMirror>> primitiveAdapter(TypeElement element) {
    return () -> element.getInterfaces().stream()
      .filter(t -> t.toString().contains("io.avaje.validation.adapter.ValidationAdapter.Primitive"))
      .findFirst();
  }

  static boolean isBasicType(final String topType) {
    return BASIC_TYPES.contains(topType)
      || topType.startsWith("java.time.")
      || GenericTypeMap.typeOfRaw(topType) != null;
  }

  static boolean isPublic(Element element) {
    var mods = element.getModifiers();
    if (mods.contains(Modifier.PUBLIC)) {
      return true;
    }
    if (mods.contains(Modifier.PRIVATE) || mods.contains(Modifier.PROTECTED)) {
      return false;
    }
    return !ProcessingContext.isImported(element);
  }
}
