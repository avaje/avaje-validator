package io.avaje.validation.generator;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

interface BeanReader {

  void read();

  void writeImports(Append writer, String adapterPackage);

  void writeFields(Append writer);

  void writeConstructor(Append writer);

  void writeValidatorMethod(Append writer);

  String shortName();

  /** Return the short name of the element. */
  default String shortName(Element element) {
    return element.getSimpleName().toString();
  }

  default int genericTypeParamsCount() {
    return 0;
  }

  TypeElement getBeanType();

  void cascadeTypes(Set<String> extraTypes);

  default boolean nonAccessibleField() {
    return false;
  }

  default boolean hasValidationAnnotation() {
    return false;
  }

  default String contraintTarget() {
    return "";
  }
}
