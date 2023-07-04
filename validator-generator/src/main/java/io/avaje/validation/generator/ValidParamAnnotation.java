package io.avaje.validation.generator;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Set;

import javax.lang.model.element.VariableElement;

public record ValidParamAnnotation(
    GenericType genericType,
    boolean hasValid,
    Map<GenericType, String> annotations,
    Map<GenericType, String> typeUse1,
    Map<GenericType, String> typeUse2) {
  static ValidParamAnnotation create(VariableElement varElement) {
    final var asString = varElement.asType().toString();

    final var noGeneric = AnnotationUtil.splitString(asString, "<")[0];

    // it seems we cannot directly retrieve mirrors from var elements
    final var annotations =
        noGeneric.transform(s -> Util.typeUse(s, false)).get(0).stream()
            .filter(not(Constants.VALID_ANNOTATIONS::contains))
            .collect(toMap(GenericType::parse, AnnotationUtil::annotationAttributeMap));

    final var rawType = Util.trimAnnotations(asString);
    final var typeUse = Util.typeUse(asString, true);
    final var typeUse1 =
        typeUse.get(0).stream()
            .collect(toMap(GenericType::parse, AnnotationUtil::annotationAttributeMap));
    final var typeUse2 =
        typeUse.get(1).stream()
            .collect(toMap(GenericType::parse, AnnotationUtil::annotationAttributeMap));

    final boolean hasValid = Constants.VALID_ANNOTATIONS.stream().anyMatch(noGeneric::contains);

    return new ValidParamAnnotation(
        GenericType.parse(rawType), hasValid, annotations, typeUse1, typeUse2);
  }

  public void addImports(Set<String> importTypes) {
    genericType.addImports(importTypes);
    annotations.keySet().forEach(t -> t.addImports(importTypes));
    typeUse1.keySet().forEach(t -> t.addImports(importTypes));
    typeUse2.keySet().forEach(t -> t.addImports(importTypes));
  }
}
