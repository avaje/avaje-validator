package io.avaje.validation.generator;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

final class FieldReader {

  private final List<String> genericTypeParams;
  private final boolean publicField;
  private final String rawType;
  private final GenericType genericType;
  private final String adapterFieldName;
  private final String adapterShortType;
  private final String fieldName;

  private MethodReader getter;
  private boolean genericTypeParameter;
  private int genericTypeParamPosition;
  private final boolean optionalValidation;
  private final Map<GenericType, String> annotations;
  private final Element element;

  FieldReader(Element element, List<String> genericTypeParams) {
    this.genericTypeParams = genericTypeParams;
    this.fieldName = element.getSimpleName().toString();
    this.publicField = element.getModifiers().contains(Modifier.PUBLIC);
    this.element = element;
    if (element instanceof ExecutableElement) {
      final var executableElement = (ExecutableElement) element;
      this.rawType = Util.trimAnnotations(executableElement.getReturnType().toString());

    } else {
      this.rawType = Util.trimAnnotations(element.asType().toString());
    }
    genericType = GenericType.parse(rawType);
    this.annotations =
        element.getAnnotationMirrors().stream()
            .collect(
                toMap(
                    a -> GenericType.parse(a.getAnnotationType().toString()),
                    AnnotationUtil::getAnnotationAttributMap));
    final String shortType = genericType.shortType();
    adapterShortType = initAdapterShortType(shortType);
    adapterFieldName = initShortName();
    this.optionalValidation = Util.isNullable(element);
  }

  private String initAdapterShortType(String shortType) {
    String typeWrapped = "ValidationAdapter<" + PrimitiveUtil.wrap(shortType) + ">";
    for (int i = 0; i < genericTypeParams.size(); i++) {
      final String typeParam = genericTypeParams.get(i);
      if (typeWrapped.contains("<" + typeParam + ">")) {
        genericTypeParameter = true;
        genericTypeParamPosition = i;
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

  String asTypeDeclaration() {
    final String asType = genericType.asTypeDeclaration().replace("? extends ", "");
    if (genericTypeParameter) {
      return genericTypeReplacement(asType, "param" + genericTypeParamPosition);
    }
    return asType;
  }

  private String genericTypeReplacement(String asType, String replaceWith) {
    final String typeParam = genericTypeParams.get(genericTypeParamPosition);
    return asType.replace(typeParam + ".class", replaceWith);
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
    final var topType = PrimitiveUtil.wrap(genericType.topType());
    if (isBasicType(topType)) {

      writer.append(";").eol();
      return;
    }

    if ("java.util.List".equals(genericType.topType())
        || "java.util.Set".equals(genericType.topType())) {
      if (isBasicType(genericType.firstParamType())) {

        writer.append(";").eol();
        return;
      }

      writer
          .eol()
          .append("           .list(ctx, %s.class)", Util.shortName(genericType.firstParamType()));
    } else if ("java.util.Map".equals(genericType.topType())) {
      if (isBasicType(genericType.secondParamType())) {

        writer.append(";").eol();
        return;
      }

      writer
          .eol()
          .append("           .map(ctx, %s.class)", Util.shortName(genericType.secondParamType()));
    } else if (genericType.topType().contains("[]")) {
      if (isBasicType(topType)) {

        writer.append(";").eol();
        return;
      }

      writer
          .eol()
          .append(
              "           .array(ctx, %s.class)",
              Util.shortName(genericType.topType().replace("[]", "")));
    } else {
      writer
          .eol()
          .append(
              "           .andThen(ctx.adapter(%s.class))", Util.shortName(genericType.topType()));
    }
    writer.append(";").eol();
  }

  private boolean isBasicType(final String topType) {
    return "java.lang.String".equals(topType) || GenericTypeMap.typeOfRaw(topType) != null;
  }
}
