package io.avaje.validation.generator;

import static io.avaje.validation.generator.ProcessingContext.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

final class Util {
  // cuts out all annotations
  private static final Pattern trimPattern =
      Pattern.compile(
          "@([a-z]+(\\.[a-z]+)+)\\([^)]*\\),|@([a-z]+(\\.[a-z]+)+)\\([^)]*\\)|@([a-z0-9]+(\\.[a-z0-9]+)+)",
          Pattern.CASE_INSENSITIVE);
  static final Pattern mapSplitString = Pattern.compile("\\s[A-Za-z0-9]+,|,");

  private Util() {}

  static boolean isValid(Element e) {
    return ValidPojoPrism.isPresent(e)
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
    return type.indexOf('.') > 0 && !type.startsWith("java.lang");
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

  static String trimAnnotations(String type) {
    final var result = String.join("", trimPattern.split(type)).replace(" ", "").replace(".,", ".");

    if (result.contains(")"))
      throw new IllegalArgumentException(
          "Right Parenthesis \")\" in TYPE_USE Annotation string arguments must be escaped with &rparen;");
    return result;
  }

  static List<List<String>> typeUse(String type) {

    final var list = new ArrayList<List<String>>(2);
    final int pos = type.indexOf('<');
    if (pos == -1 || type.indexOf('@') == -1) {
      return List.of(List.of(),List.of());
    }
    final var trimmed = trimAnnotations(type);
    final var str = type.substring(pos + 1, type.lastIndexOf('>'));

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

  private static List<String> extractTypeUseAnnotations(final String str) {

    final var list = new ArrayList<String>();
    final var matcher = trimPattern.matcher(str);
    while (matcher.find()) {

      var str2 = matcher.group().substring(1);

      if (str2.endsWith(",")) {
        str2 = str2.substring(0, str2.length() - 1);
      }
      list.add(str2);
    }
    return list;
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

  /**
   * Return the base type given the JsonAdapter type. Remove the "jsonb" sub-package and the
   * "JsonAdapter" suffix.
   */
  static String baseTypeOfAdapter(String adapterFullName) {
    return element(adapterFullName).getInterfaces().stream()
        .filter(t -> t.toString().contains("io.avaje.validation.adapter.ValidationAdapter"))
        .findFirst()
        .map(Object::toString)
        .map(GenericType::parse)
        .map(GenericType::firstParamType)
        .map(Util::extractTypeWithNest)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Adapter: "
                        + adapterFullName
                        + " does not directly implement ValidationAdapter"));
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
}
