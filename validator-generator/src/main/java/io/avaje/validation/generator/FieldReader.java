package io.avaje.validation.generator;

import static java.util.stream.Collectors.toList;

import java.util.List;
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
  private final List<GenericType> annotations;

  FieldReader(Element element, List<String> genericTypeParams) {
    this.genericTypeParams = genericTypeParams;
    this.fieldName = element.getSimpleName().toString();
    this.publicField = element.getModifiers().contains(Modifier.PUBLIC);

    if (element instanceof ExecutableElement) {
      final var executableElement = (ExecutableElement) element;
      this.rawType = Util.trimAnnotations(executableElement.getReturnType().toString());

    } else {
      this.rawType = Util.trimAnnotations(element.asType().toString());
    }
    genericType = GenericType.parse(rawType);
    this.annotations =
        element.getAnnotationMirrors().stream()
            .map(a -> GenericType.parse(a.getAnnotationType().toString()))
            .collect(toList());
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

    genericType.addImports(importTypes);
    annotations.forEach(t -> t.addImports(importTypes));
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

  void writeFromJsonSwitch(Append writer) {

    writer.append("    %s.validate(", adapterFieldName);
    writeGetValue(writer, "");
    writer.append(", request, \"%s\");", adapterFieldName);

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
    writer.append("    this.%s = ctx.adapter(%s);", adapterFieldName, asTypeDeclaration()).eol();
  }
}
