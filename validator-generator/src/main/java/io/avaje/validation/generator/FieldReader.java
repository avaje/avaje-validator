package io.avaje.validation.generator;

import static io.avaje.validation.generator.PrimitiveUtil.isPrimitiveValidationType;
import static io.avaje.validation.generator.APContext.logError;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

final class FieldReader {

  private final List<String> genericTypeParams;
  private final boolean publicField;
  private final UType genericType;
  private final String adapterFieldName;
  private final String adapterShortType;
  private final String fieldName;

  private MethodReader getter;
  private boolean genericTypeParameter;
  private final boolean optionalValidation;
  private final Element element;
  private final ElementAnnotationContainer elementAnnotations;
  private final boolean classLevel;
  private final boolean usePrimitiveValidation;

  FieldReader(Element element, List<String> genericTypeParams) {
    this(element, genericTypeParams, false);
  }

  FieldReader(Element element, List<String> genericTypeParams, boolean classLevel) {
    this.genericTypeParams = genericTypeParams;
    this.fieldName = element.getSimpleName().toString();
    this.publicField = Util.isPublic(element);
    this.element = element;
    this.elementAnnotations = ElementAnnotationContainer.create(element);
    this.genericType = elementAnnotations.genericType();
    final String shortType = genericType.shortWithoutAnnotations();
    usePrimitiveValidation = isPrimitiveValidationType(shortType) && elementAnnotations.supportsPrimitiveValidation();
    adapterShortType = initAdapterShortType(shortType);
    adapterFieldName = initShortName();
    this.optionalValidation = Util.isNullable(element);
    this.classLevel = classLevel;
  }

  FieldReader(TypeElement baseType, TypeElement mixInType, List<String> genericTypeParams) {
    this.genericTypeParams = genericTypeParams;
    this.fieldName = baseType.getSimpleName().toString();
    this.publicField = Util.isPublic(baseType);
    this.element = baseType;
    this.elementAnnotations = ElementAnnotationContainer.create(mixInType);
    this.genericType = UType.parse(baseType.asType());
    final String shortType = genericType.shortWithoutAnnotations();
    usePrimitiveValidation =
        isPrimitiveValidationType(shortType) && elementAnnotations.supportsPrimitiveValidation();
    adapterShortType = initAdapterShortType(shortType);
    adapterFieldName = initShortName();
    this.optionalValidation = Util.isNullable(mixInType);
    this.classLevel = true;
  }

  private String initAdapterShortType(String shortType) {
    if (usePrimitiveValidation) {
      return "ValidationAdapter.Primitive";
    }
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

  String fieldName() {
    return fieldName;
  }

  boolean typeObjectBooleanWithIsPrefix() {
    return nameHasIsPrefix() && "java.lang.Boolean".equals(genericType.mainType());
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
    importTypes.addAll(genericType.importTypes());
    elementAnnotations.addImports(importTypes);
  }

  void cascadeTypes(Set<String> types) {
    final String mainType = genericType.mainType();
    if ("java.util.List".equals(mainType) || "java.util.Set".equals(mainType)) {
      types.add(genericType.param0().fullWithoutAnnotations());
    } else if ("java.util.Map".equals(mainType)) {
      types.add(genericType.param1().fullWithoutAnnotations());
    } else {
      types.add(mainType);
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
      logError(element, "Field" + fieldName + " is inaccessible. Add a getter or make the field package-private/public.");
    }
  }

  void writeValidate(Append writer) {
    if (classLevel) {
      writer.append(
          """
    		    if (!request.hasViolations()) {
    		      %s.validate(value, request, field);
    		    }
    		""",
          adapterFieldName);
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

  void writeConstructor(Append writer) {
    writer.append("    this.%s = ", adapterFieldName).eol();

    new AdapterHelper(
            writer,
            elementAnnotations,
            "        ",
            PrimitiveUtil.wrap(genericType.shortWithoutAnnotations()),
            genericType,
            classLevel)
        .usePrimitiveValidation(usePrimitiveValidation)
        .write();
    writer.append(";").eol().eol();
  }

  boolean isClassLvl() {
    return classLevel;
  }

  boolean hasConstraints() {
    return !elementAnnotations.isEmpty();
  }
}
