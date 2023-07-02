package io.avaje.validation.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A type with generic parameters and potentially nested. */
final class GenericType {

  /** Trim off generic wildcard from the raw type if present. */
  static String trimWildcard(String rawType) {
    if (rawType.endsWith("<?>")) {
      return rawType.substring(0, rawType.length() - 3);
    }
    return rawType;
  }

  private final String raw;
  private String mainType;

  private final List<GenericType> params = new ArrayList<>();

  /** Create for top level type. */
  GenericType(String raw) {
    this.raw = raw.transform(s -> s.startsWith(",") ? s.substring(1) : s);
  }

  /** Create for parameter type. */
  GenericType() {
    this.raw = null;
  }

  /** Return true if this is a generic type. */
  static boolean isGeneric(String raw) {
    return raw.contains("<");
  }

  /** Parse and return as GenericType. */
  static GenericType parse(String raw) {
    raw = trimWildcard(raw);
    if (raw.indexOf('<') == -1) {
      return new GenericType(raw);
    }
    return new GenericTypeParser(raw).parse();
  }

  @Override
  public String toString() {
    return raw != null ? raw : mainType + '<' + params + '>';
  }

  void addImports(Set<String> importTypes) {
    final String type = trimExtends();
    if (includeInImports(type)) {
      importTypes.add(type);
    }
    for (final GenericType param : params) {
      param.addImports(importTypes);
    }
  }

  private static boolean includeInImports(String type) {
    return type != null && !type.startsWith("java.lang.") && type.contains(".");
  }

  String shortType() {
    final StringBuilder sb = new StringBuilder();
    writeShortType(sb);
    return sb.toString();
  }

  /** Append the short version of the type (given the type and parameters are in imports). */
  void writeShortType(StringBuilder sb) {
    final String main = Util.shortName(trimExtends());
    sb.append(main);
    final int paramCount = params.size();
    if (paramCount > 0) {
      sb.append("<");
      for (int i = 0; i < paramCount; i++) {
        if (i > 0) {
          sb.append(",");
        }
        params.get(i).writeShortType(sb);
      }
      sb.append(">");
    }
  }

  void writeType(String prefix, StringBuilder sb) {
    final String main = Util.shortName(trimExtends());
    sb.append(prefix).append(main).append(".class");
    final int paramCount = params.size();
    if (paramCount > 0) {
      for (final GenericType param : params) {
        param.writeType(",", sb);
      }
    }
  }

  String shortName() {
    final StringBuilder sb = new StringBuilder();
    shortName(sb);
    return sb.toString().replace("[]", "Array");
  }

  void shortName(StringBuilder sb) {
    sb.append(Util.shortName(trimExtends()));
    for (final GenericType param : params) {
      param.shortName(sb);
    }
  }

  private String trimExtends() {
    String type = topType();
    final var index = type.indexOf("(");
    type = index == -1 ? type : type.substring(0, index);

    if (type != null && type.startsWith("? extends ")) {
      return type.substring(10);
    }
    return type;
  }

  String topType() {
    return mainType != null ? mainType : raw;
  }

  void setMainType(String mainType) {
    this.mainType = mainType;
  }

  void addParam(GenericType param) {
    params.add(param);
  }

  String firstParamType() {
    return params.isEmpty() ? "java.lang.Object" : params.get(0).topType();
  }

  String secondParamType() {
    return params.size() != 2 ? "java.lang.Object" : params.get(1).topType();
  }
}
