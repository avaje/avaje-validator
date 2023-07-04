package io.avaje.validation.generator;

import static java.util.stream.Collectors.toMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

final class FieldReader {

  static final Set<String> BASIC_TYPES = Set.of("java.lang.String", "java.math.BigDecimal");

  private final List<String> genericTypeParams;
  private final boolean publicField;
  private final String rawType;
  private final GenericType genericType;
  private final String adapterFieldName;
  private final String adapterShortType;
  private final String fieldName;

  private MethodReader getter;
  private boolean genericTypeParameter;
  private final boolean optionalValidation;
  private final Map<GenericType, String> annotations;
  private final Element element;
  private final Map<GenericType, String> typeUse1;
  private final Map<GenericType, String> typeUse2;
  private final boolean hasValid;

  FieldReader(Element element, List<String> genericTypeParams) {
    this.genericTypeParams = genericTypeParams;
    this.fieldName = element.getSimpleName().toString();
    this.publicField = element.getModifiers().contains(Modifier.PUBLIC);
    this.element = element;
    this.hasValid = ValidPrism.isPresent(element);
    if (element instanceof final ExecutableElement executableElement) {
      this.rawType = Util.trimAnnotations(executableElement.getReturnType().toString());
      final var typeUse = Util.typeUse(executableElement.getReturnType().toString(),true);
      typeUse1 =
          typeUse.get(0).stream()
              .collect(toMap(GenericType::parse, AnnotationUtil::annotationAttributeMap));
      typeUse2 =
          typeUse.get(1).stream()
              .collect(toMap(GenericType::parse, AnnotationUtil::annotationAttributeMap));

    } else {
      this.rawType = Util.trimAnnotations(element.asType().toString());
      final var typeUse = Util.typeUse(element.asType().toString(),true);
      typeUse1 =
          typeUse.get(0).stream()
              .collect(toMap(GenericType::parse, AnnotationUtil::annotationAttributeMap));
      typeUse2 =
          typeUse.get(1).stream()
              .collect(toMap(GenericType::parse, AnnotationUtil::annotationAttributeMap));
    }
    genericType = GenericType.parse(rawType);

    this.annotations =
        element.getAnnotationMirrors().stream()
            .collect(
                toMap(
                    a -> GenericType.parse(a.getAnnotationType().toString()),
                    AnnotationUtil::annotationAttributeMap));
    final String shortType = genericType.shortType();
    adapterShortType = initAdapterShortType(shortType);
    adapterFieldName = initShortName();
    this.optionalValidation = Util.isNullable(element);
  }

  private String initAdapterShortType(String shortType) {
    String typeWrapped = "ValidationAdapter<" + PrimitiveUtil.wrap(shortType) + ">";
    for (final String typeParam : genericTypeParams) {
      if (typeWrapped.contains("<" + typeParam + ">")) {
        genericTypeParameter = true;
        typeWrapped = typeWrapped.replace("<" + typeParam + ">", "<Object>");
      }
    }
    return typeWrapped;
  }

  private String initShortName() {
    if (genericTypeParameter) {
      return Util.initLower(fieldName) + "ValidationAdapterGeneric";
    }
    return Util.initLower(fieldName) + "ValidationAdapter";
  }

  static String trimAnnotations(String type) {
    final int pos = type.indexOf("@");
    if (pos == -1) {
      return type;
    }
    return type.substring(0, pos) + type.substring(type.lastIndexOf(' ') + 1);
  }

  String fieldName() {
    return fieldName;
  }

  boolean typeObjectBooleanWithIsPrefix() {
    return nameHasIsPrefix() && "java.lang.Boolean".equals(genericType.topType());
  }

  boolean typeBooleanWithIsPrefix() {
    return nameHasIsPrefix()
        && ("boolean".equals(genericType.topType())
            || "java.lang.Boolean".equals(genericType.topType()));
  }

  private boolean nameHasIsPrefix() {
    return fieldName.length() > 2
        && fieldName.startsWith("is")
        && Character.isUpperCase(fieldName.charAt(2));
  }

  void addImports(Set<String> importTypes) {
    if (PatternPrism.isPresent(element)) {
      importTypes.add("static io.avaje.validation.adapter.RegexFlag.*");
    }
    genericType.addImports(importTypes);
    annotations.keySet().forEach(t -> t.addImports(importTypes));
    typeUse1.keySet().forEach(t -> t.addImports(importTypes));
    typeUse2.keySet().forEach(t -> t.addImports(importTypes));
  }

  void cascadeTypes(Set<String> types) {
    final String topType = genericType.topType();
    if ("java.util.List".equals(topType) || "java.util.Set".equals(topType)) {
      types.add(genericType.firstParamType());
    } else if ("java.util.Map".equals(topType)) {
      types.add(genericType.secondParamType());
    } else {
      types.add(topType);
    }
  }

  void getterMethod(MethodReader getter) {
    if (getter != null) {
      this.getter = getter;
    }
  }

  boolean isPublicField() {
    return publicField;
  }

  String adapterShortType() {
    return genericType.shortType();
  }

  void writeField(Append writer) {
    writer.append("  private final %s %s;", adapterShortType, adapterFieldName).eol();
  }

  private void writeGetValue(Append writer, String suffix) {
    if (getter != null) {
      writer.append("value.%s()%s", getter.getName(), suffix);
    } else if (publicField) {
      writer.append("value.%s%s", fieldName, suffix);
    } else {
      throw new IllegalStateException(
          "Field" + fieldName + " is inaccessible. Add a getter or make the field public.");
    }
  }

  void writeValidate(Append writer) {
    writer.append("    var _$%s = ", fieldName);
    writeGetValue(writer, ";");
    writer.eol();
    if (optionalValidation) {
      writer.append("    if(_$%s != null) {", fieldName);
    }
    writer.append("    %s.validate(_$%s", adapterFieldName, fieldName);
    writer.append(", request, \"%s\");", fieldName);
    if (optionalValidation) {
      writer.append("    }");
    }
    writer.eol().eol();
  }

  @Override
  public String toString() {
    return fieldName;
  }

  public GenericType type() {
    return genericType;
  }

  public void writeConstructor(Append writer) {
    writer.append("    this.%s = ", adapterFieldName).eol();

    boolean first = true;
    for (final var a : annotations.entrySet()) {
      if (first) {
        writer.append(
            "        ctx.<%s>adapter(%s.class, %s)",
            PrimitiveUtil.wrap(genericType.shortType()), a.getKey().shortName(), a.getValue());
        first = false;
        continue;
      }
      writer
          .eol()
          .append(
              "            .andThen(ctx.adapter(%s.class,%s))",
              a.getKey().shortName(), a.getValue());
    }
    final var topType = genericType.topType();
    if (Util.isBasicType(topType)) {
      writer.append(";").eol();
      return;
    }

    if (annotations.isEmpty()) {
      writer.append("        ctx.<%s>noop()", PrimitiveUtil.wrap(genericType.shortType()));
    }

    if (!typeUse1.isEmpty()
        && ("java.util.List".equals(genericType.topType())
            || "java.util.Set".equals(genericType.topType()))) {
      writer.eol().append("            .list()");
      final var t = genericType.firstParamType();
      writeTypeUse(writer, t, typeUse1);

    } else if ((!typeUse1.isEmpty() || !typeUse2.isEmpty())
        && "java.util.Map".equals(genericType.topType())) {

      writer.eol().append("            .mapKeys()");
      writeTypeUse(writer, genericType.firstParamType(), typeUse1);

      writer.eol().append("            .mapValues()");
      writeTypeUse(writer, genericType.secondParamType(), typeUse2, false);

    } else if (genericType.topType().contains("[]") && hasValid) {

      writer.eol().append("            .array()");
      writeTypeUse(writer, genericType.firstParamType(), typeUse1);
    } else if (hasValid) {
      writer
          .eol()
          .append(
              "            .andThen(ctx.adapter(%s.class))", Util.shortName(genericType.topType()));
    }
    writer.append(";").eol().eol();
  }

  private void writeTypeUse(
      Append writer, String firstParamType, Map<GenericType, String> typeUse12) {
    writeTypeUse(writer, firstParamType, typeUse12, true);
  }

  private void writeTypeUse(
      Append writer, String t, Map<GenericType, String> typeUseMap, boolean keys) {

    for (final var a : typeUseMap.entrySet()) {

      if (Constants.VALID_ANNOTATIONS.contains(a.getKey().topType())) {
        continue;
      }
      final var k = a.getKey().shortName();
      final var v = a.getValue();
      writer.eol().append("            .andThenMulti(ctx.adapter(%s.class,%s))", k, v);
    }

    if (!Util.isBasicType(t)
        && typeUseMap.keySet().stream()
            .map(GenericType::topType)
            .anyMatch(Constants.VALID_ANNOTATIONS::contains)) {

      writer
          .eol()
          .append(
              "           .andThenMulti(ctx.adapter(%s.class))",
              Util.shortName(keys ? genericType.firstParamType() : genericType.secondParamType()))
          .eol();
    }
  }
}
