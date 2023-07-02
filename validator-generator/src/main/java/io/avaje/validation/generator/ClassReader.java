package io.avaje.validation.generator;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.TypeElement;

final class ClassReader implements BeanReader {

  private final TypeElement beanType;
  private final String shortName;
  private final String type;
  private final List<FieldReader> allFields;
  private final Set<String> importTypes = new TreeSet<>();
  private final TypeReader typeReader;
  private final boolean nonAccessibleField;

  ClassReader(TypeElement beanType) {
    this.beanType = beanType;
    this.type = beanType.getQualifiedName().toString();
    this.shortName = shortName(beanType);
    this.typeReader = new TypeReader(beanType);
    typeReader.process();
    this.nonAccessibleField = typeReader.nonAccessibleField();
    this.allFields = typeReader.allFields();
    importTypes.add("java.util.List");
    importTypes.add("java.util.Set");
    importTypes.add("java.util.Map");

    importTypes.add("io.avaje.validation.adapter.ValidationAdapter");
    importTypes.add("io.avaje.validation.adapter.ValidationContext");
    importTypes.add("io.avaje.validation.adapter.ValidationRequest");
    importTypes.add("io.avaje.validation.spi.Generated");
  }

  @Override
  public int genericTypeParamsCount() {
    return typeReader.genericTypeParamsCount();
  }

  @Override
  public String toString() {
    return beanType.toString();
  }

  @Override
  public String shortName() {
    return shortName;
  }

  @Override
  public TypeElement getBeanType() {
    return beanType;
  }

  List<FieldReader> allFields() {
    return allFields;
  }

  @Override
  public boolean nonAccessibleField() {
    return nonAccessibleField;
  }

  @Override
  public boolean hasJsonAnnotation() {
    return Util.isValid(beanType);
  }

  @Override
  public void read() {
    for (final FieldReader field : allFields) {
      field.addImports(importTypes);
    }
  }

  private Set<String> importTypes() {
    if (Util.validImportType(type)) {
      importTypes.add(type);
    }
    for (final FieldReader allField : allFields) {
      allField.addImports(importTypes);
    }
    return importTypes;
  }

  @Override
  public void writeImports(Append writer) {
    for (final String importType : importTypes()) {
      if (Util.validImportType(importType)) {
        writer.append("import %s;", importType).eol();
      }
    }
    writer.eol();
  }

  @Override
  public void cascadeTypes(Set<String> types) {
    for (final FieldReader allField : allFields) {
      allField.cascadeTypes(types);
    }
  }

  @Override
  public void writeFields(Append writer) {

    for (final FieldReader allField : allFields) {
      allField.writeField(writer);
    }
    writer.eol();
  }

  @Override
  public void writeConstructor(Append writer) {
    for (final FieldReader allField : allFields) {
      allField.writeConstructor(writer);
    }
  }

  @Override
  public void writeValidatorMethod(Append writer) {
    writer.eol();
    writer.append("  @Override").eol();
    writer.append("  public boolean validate(%s value, ValidationRequest request, String propertyName) {", shortName).eol();
    writer.append("    if (propertyName != null) {").eol();
    writer.append("      request.pushPath(propertyName);").eol();
    writer.append("    }").eol();
    for (final FieldReader allField : allFields) {
      allField.writeValidate(writer);
    }
    writer.append("    if (propertyName != null) {").eol();
    writer.append("      request.popPath();").eol();
    writer.append("    }").eol();
    writer.append("    return true;", shortName).eol();
    writer.append("  }").eol();
  }
}
