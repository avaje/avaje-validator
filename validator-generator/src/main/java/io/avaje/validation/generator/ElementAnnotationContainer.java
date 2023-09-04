package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.typeElement;
import static io.avaje.validation.generator.PrimitiveUtil.isPrimitiveValidationAnnotations;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public record ElementAnnotationContainer(
    UType genericType,
    boolean hasValid,
    Map<UType, String> annotations,
    Map<UType, String> typeUse1,
    Map<UType, String> typeUse2,
    Map<UType, String> crossParam) {

  static ElementAnnotationContainer create(Element element, boolean classLevel) {

    Map<UType, String> typeUse1;
    Map<UType, String> typeUse2;

    final Map<UType, String> crossParam = new HashMap<>();
    UType uType;
    if (element instanceof final ExecutableElement executableElement) {
      uType = UType.parse(executableElement.getReturnType());
    } else {
      uType = UType.parse(element.asType());
    }

    final Map<UType, String> annotations;

    final boolean hasValid;
    if (element instanceof final VariableElement varElement) {
      hasValid = uType.annotations().stream().anyMatch(ValidPrism::isInstance);

      annotations =
          uType.annotations().stream()
              .filter(m -> !ValidPrism.isInstance(m))
              .collect(
                  toMap(
                      a -> UType.parse(a.getAnnotationType()),
                      a -> AnnotationUtil.annotationAttributeMap(a, varElement)));
    } else {
      hasValid = ValidPrism.isPresent(element);
      annotations =
          element.getAnnotationMirrors().stream()
              .filter(m -> !ValidPrism.isInstance(m))
              .filter(m -> !classLevel || hasMetaConstraintAnnotation(m))
              .map(
                  a -> {
                    if (CrossParamConstraintPrism.isPresent(a.getAnnotationType().asElement())) {
                      crossParam.put(
                          UType.parse(a.getAnnotationType()),
                          AnnotationUtil.annotationAttributeMap(a, element));
                      return null;
                    }
                    return a;
                  })
              .filter(Objects::nonNull)
              .collect(
                  toMap(
                      a -> UType.parse(a.getAnnotationType()),
                      a -> AnnotationUtil.annotationAttributeMap(a, element)));
    }

    typeUse1 =
        Optional.ofNullable(uType.param0()).map(UType::annotations).stream()
            .flatMap(List::stream)
            .filter(m -> !classLevel || hasMetaConstraintAnnotation(m))
            .collect(
                toMap(
                    a -> UType.parse(a.getAnnotationType()),
                    a -> AnnotationUtil.annotationAttributeMap(a, element)));

    typeUse2 =
        Optional.ofNullable(uType.param1()).map(UType::annotations).stream()
            .flatMap(List::stream)
            .filter(m -> !classLevel || hasMetaConstraintAnnotation(m))
            .collect(
                toMap(
                    a -> UType.parse(a.getAnnotationType()),
                    a -> AnnotationUtil.annotationAttributeMap(a, element)));
    return new ElementAnnotationContainer(
        uType, hasValid, annotations, typeUse1, typeUse2, crossParam);
  }

  static ElementAnnotationContainer create(VariableElement element) {
    return create(element, false);
  }

  static boolean hasMetaConstraintAnnotation(AnnotationMirror m) {
    return hasMetaConstraintAnnotation(m.getAnnotationType().asElement());
  }

  static boolean hasMetaConstraintAnnotation(Element element) {
    return ConstraintPrism.isPresent(element);
  }

  public void addImports(Set<String> importTypes) {
    importTypes.addAll(genericType.importTypes());
    annotations.keySet().forEach(t -> importTypes.addAll(t.importTypes()));
    typeUse1.keySet().forEach(t -> importTypes.addAll(t.importTypes()));
    typeUse2.keySet().forEach(t -> importTypes.addAll(t.importTypes()));
  }

  boolean isEmpty() {
    return annotations.isEmpty() && typeUse1.isEmpty() && typeUse2.isEmpty();
  }

  boolean supportsPrimitiveValidation() {
    for (final var validationAnnotation : annotations.keySet()) {
      ConstraintPrism.getOptionalOn(typeElement(validationAnnotation.full()))
          .ifPresent(
              p -> {
                if (p.unboxPrimitives()) {
                  validationAnnotation
                      .shortType()
                      .transform(PrimitiveUtil::addPrimitiveValidationAnnotation);
                }
              });

      if (!isPrimitiveValidationAnnotations(validationAnnotation.shortType())) {
        return false;
      }
    }
    return true;
  }
}
