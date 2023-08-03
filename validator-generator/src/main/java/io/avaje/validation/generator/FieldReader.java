package io.avaje.validation.generator;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

final class FieldReader {

  static final Set<String> BASIC_TYPES = Set.of("java.lang.String", "java.math.BigDecimal");

  private final List<String> genericTypeParams;
  private final boolean publicField;
  private final GenericType genericType;
  private final String adapterFieldName;
  private final String adapterShortType;
  private final String fieldName;

  private MethodReader getter;
  private boolean genericTypeParameter;
  private final boolean optionalValidation;
  private final Element element;
  private final ElementAnnotationContainer elementAnnotations;
  private final boolean classLevel;

  FieldReader(Element element, List<String> genericTypeParams) {
    this.genericTypeParams = genericTypeParams;
    this.fieldName = element.getSimpleName().toString();
    this.publicField = element.getModifiers().contains(Modifier.PUBLIC);
    this.element = element;
    this.elementAnnotations = ElementAnnotationContainer.create(element);
    this.genericType = elementAnnotations.genericType();
    final String shortType = genericType.shortType();
    adapterShortType = initAdapterShortType(shortType);
    adapterFieldName = initShortName();
    this.optionalValidation = Util.isNullable(element);
    this.classLevel = element instanceof TypeElement;
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
    elementAnnotations.addImports(importTypes);
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
    if (classLevel) {
      // don't need a getter
    } else if (getter != null) {
      writer.append("value.%s()%s", getter.getName(), suffix);
    } else if (publicField) {
      writer.append("value.%s%s", fieldName, suffix);
    } else {
      throw new IllegalStateException(
          "Field" + fieldName + " is inaccessible. Add a getter or make the field public.");
    }
  }

  void writeValidate(Append writer) {
    if (classLevel) {
      writer.append("    %s.validate(value, request, field);", adapterFieldName);
      writer.eol().eol();
      return;
    }
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

    AdapterHelper.writeAdapterWithValues(
        writer, elementAnnotations, "        ", PrimitiveUtil.wrap(genericType.shortType()));
    writer.append(";").eol().eol();
  }

  public boolean isClassLvl() {
    return classLevel;
  }

  public boolean hasAnnotations() {
    return !elementAnnotations.isEmpty();
  }
}
