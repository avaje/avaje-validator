package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.typeElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static io.avaje.validation.generator.APContext.logError;

final class Util {

  private static final Pattern WHITE_SPACE_REGEX =
      Pattern.compile("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
  private static final Pattern COMMA_PATTERN =
      Pattern.compile(", (?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");

  static final Pattern mapSplitString = Pattern.compile("\\s[A-Za-z0-9]+,|,");
  static final Set<String> BASIC_TYPES = Set.of("java.lang.String", "java.math.BigDecimal");

  private Util() {}

  static boolean isValid(Element e) {
    return AvajeValidPrism.isPresent(e)
        || JavaxValidPrism.isPresent(e)
        || JakartaValidPrism.isPresent(e);
  }

  /** Return true if the element has a Nullable annotation. */
  public static boolean isNullable(Element p) {
    for (final AnnotationMirror mirror : p.getAnnotationMirrors()) {
      if ("Nullable".equalsIgnoreCase(shortName(mirror.getAnnotationType().toString()))) {
        return true;
      }
    }
    return false;
  }

  static boolean validImportType(String type) {
    return type.indexOf('.') > 0 && !type.startsWith("java.lang.")
        || (type.startsWith("java.lang.")
            && type.replace("java.lang.", "").transform(s -> s.contains(".")));
  }

  static String packageOf(String cls) {
    final int pos = cls.lastIndexOf('.');
    return pos == -1 ? "" : cls.substring(0, pos);
  }

  static String shortName(String fullType) {
    final int p = fullType.lastIndexOf('.');
    if (p == -1) {
      return fullType;
    }
    return fullType.substring(p + 1);
  }

  static String shortType(String fullType) {
    final int p = fullType.lastIndexOf('.');
    if (p == -1) {
      return fullType;
    }
    if (fullType.startsWith("java")) {
      return fullType.substring(p + 1);
    } else {
      var result = "";
      var foundClass = false;
      for (final String part : fullType.split("\\.")) {
        if (foundClass || Character.isUpperCase(part.charAt(0))) {
          foundClass = true;
          result += (result.isEmpty() ? "" : ".") + part;
        }
      }
      return result;
    }
  }

  public static String trimAnnotations(String input) {
    input = COMMA_PATTERN.matcher(input).replaceAll(",");
    return cutAnnotations(input);
  }

  private static String cutAnnotations(String input) {
    final int pos = input.indexOf("@");
    if (pos == -1) {
      return input;
    }

    final Matcher matcher = WHITE_SPACE_REGEX.matcher(input);

    int currentIndex = 0;
    if (matcher.find()) {
      currentIndex = matcher.start();
    }
    final var result = input.substring(0, pos) + input.substring(currentIndex + 1);
    return cutAnnotations(result);
  }

  static List<List<String>> typeUse(String type, boolean genericOnly) {
    final var list = new ArrayList<List<String>>(2);
    final int pos = type.indexOf('<');
    if (type.indexOf('@') == -1 || genericOnly && pos == -1) {
      return List.of(List.of(), List.of());
    }
    final var trimmed = trimAnnotations(type);
    var str = type;
    if (pos > 0) {
      str = type.substring(pos + 1, type.lastIndexOf('>'));
    }

    if (trimmed.startsWith("java.util.Map")) {
      final var mapArgs = splitStringWithRegex(str);
      final var first = mapArgs[0];
      final var second = mapArgs[1];
      if (first.indexOf('@') == -1) {
        list.add(List.of());
      } else {
        list.add(extractTypeUseAnnotations(first));
      }

      if (second.indexOf('@') == -1) {
        list.add(List.of());
      } else {
        list.add(extractTypeUseAnnotations(second));
      }
    } else {
      list.add(extractTypeUseAnnotations(str));
      list.add(List.of());
    }

    return list;
  }

  private static List<String> extractTypeUseAnnotations(String input) {
    final List<String> list = new ArrayList<>();
    input = COMMA_PATTERN.matcher(input).replaceAll(",");
    final var str2 =
        retrieveAnnotations(input, "")
            .trim()
            .transform(s -> s.endsWith(",") ? s.substring(0, s.length() - 1) : s);

    Arrays.stream(AnnotationUtil.splitString(str2, ",@"))
        .map(String::trim)
        .map(s -> s.startsWith("@") ? s.substring(1) : s)
        .forEach(list::add);

    return list;
  }

  private static String retrieveAnnotations(String starter, String input) {
    final int pos = starter.indexOf("@");
    if (pos == -1) {
      return input + ",";
    }

    final Matcher matcher = WHITE_SPACE_REGEX.matcher(starter);
    int currentIndex = 0;
    if (matcher.find()) {
      currentIndex = matcher.start();
    }
    final var result = starter.substring(pos, currentIndex);
    return retrieveAnnotations(input.replace(result, ""), result);
  }

  private static String[] splitStringWithRegex(String input) {
    final Matcher matcher = mapSplitString.matcher(input);

    int startIndex = 0;
    final List<String> result = new ArrayList<>();

    while (matcher.find()) {
      final int matchStart = matcher.start();
      final int matchEnd = matcher.end();
      if (!withinQuotes(input, matchStart)
          && !input.substring(startIndex, matchEnd - 1).endsWith(")")) {
        result.add(input.substring(startIndex, matchEnd - 1).trim());
        startIndex = matchEnd;
        break;
      }
    }

    result.add(input.substring(startIndex).trim());
    return result.toArray(new String[0]);
  }

  private static boolean withinQuotes(String input, int index) {
    int quoteCount = 0;

    for (int i = 0; i < index; i++) {
      if (input.charAt(i) == '"') {
        quoteCount++;
      }
    }

    return quoteCount % 2 != 0;
  }

  static String stripBrackets(String fullType) {
    return fullType.substring(1, fullType.length() - 1);
  }

  /** Return the common parent package. */
  static String commonParent(String currentTop, String aPackage) {
    if (aPackage == null) return currentTop;
    if (currentTop == null) return packageOf(aPackage);
    if (aPackage.startsWith(currentTop)) {
      return currentTop;
    }
    int next;
    do {
      next = currentTop.lastIndexOf('.');
      if (next > -1) {
        currentTop = currentTop.substring(0, next);
        if (aPackage.startsWith(currentTop)) {
          return currentTop;
        }
      }
    } while (next > -1);

    return currentTop;
  }

  static String initCap(String input) {
    if (input.length() < 2) {
      return input.toUpperCase();
    }
    return Character.toUpperCase(input.charAt(0)) + input.substring(1);
  }

  static String escapeQuotes(String input) {
    return input.replaceAll("^\"|\"$", "\\\\\"");
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

  /** Return the base type given the ValidationAdapter type. */
  static String baseTypeOfAdapter(String adapterFullName) {
    final var element = typeElement(adapterFullName);
    if (element == null) {
      throw new NullPointerException("Element not found for [" + adapterFullName + "]");
    }
    return baseTypeOfAdapter(element);
  }

  static String baseTypeOfAdapter(TypeElement element) {

    return Optional.of(element.getSuperclass())
        .filter(
            t ->
                t.toString().contains("io.avaje.validation.adapter.AbstractConstraintAdapter")
                    || t.toString().contains("io.avaje.validation.adapter.PrimitiveAdapter"))
        .or(validationAdapter(element))
        .map(Object::toString)
        .map(GenericType::parse)
        .map(GenericType::firstParamType)
        .map(Util::extractTypeWithNest)
        .orElseGet(
            () -> {
              logError(
                  element,
                  "Custom Constraint adapters must extend AbstractConstraintAdapter or implement ValidationAdapter");
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
    return () ->
        element.getInterfaces().stream()
            .filter(
                t ->
                    t.toString().contains("io.avaje.validation.adapter.ValidationAdapter")
                        || t.toString()
                            .contains("io.avaje.validation.adapter.ValidationAdapter.Primitive"))
            .findFirst();
  }

  private static Supplier<Optional<? extends TypeMirror>> primitiveAdapter(TypeElement element) {
    return () ->
        element.getInterfaces().stream()
            .filter(
                t ->
                    t.toString()
                        .contains("io.avaje.validation.adapter.ValidationAdapter.Primitive"))
            .findFirst();
  }

  static String extractTypeWithNest(String fullType) {
    final int p = fullType.lastIndexOf('.');
    if (p == -1 || fullType.startsWith("java")) {
      return fullType;
    } else {
      final StringBuilder result = new StringBuilder();
      var foundClass = false;
      var firstClass = true;
      for (final String part : fullType.split("\\.")) {
        if (Character.isUpperCase(part.charAt(0))) {
          foundClass = true;
        }
        result.append(foundClass && !firstClass ? "$" : ".").append(part);
        if (foundClass) {
          firstClass = false;
        }
      }
      if (result.charAt(0) == '.') {
        result.deleteCharAt(0);
      }
      return result.toString();
    }
  }

  static boolean isBasicType(final String topType) {
    return BASIC_TYPES.contains(topType)
        || topType.startsWith("java.time.")
        || GenericTypeMap.typeOfRaw(topType) != null;
  }
}
