package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.typeElement;
import static io.avaje.validation.generator.PrimitiveUtil.isPrimitiveValidationAnnotations;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

record ElementAnnotationContainer(
    UType genericType,
    boolean hasValid,
    List<Entry<UType, String>> annotations,
    List<Entry<UType, String>> typeUse1,
    List<Entry<UType, String>> typeUse2,
    List<Entry<UType, String>> crossParam) {

  static ElementAnnotationContainer create(Element element) {
    UType uType;
    if (element instanceof final ExecutableElement executableElement) {
      uType = UType.parse(executableElement.getReturnType());
    } else {
      uType = UType.parse(element.asType());
    }

    final var hasValid =
      ValidPrism.isPresent(element)
        || uType.annotations().stream().anyMatch(ValidPrism::isInstance);

    List<Entry<UType, String>> typeUse1 = typeUseFor(uType.param0(), element);
    List<Entry<UType, String>> typeUse2 = typeUseFor(uType.param1(), element);

    final List<Entry<UType, String>> crossParam = new ArrayList<>();
    final var annotations = annotations(element, uType, crossParam);

    if (Util.isNonNullable(element)) {
      var nonNull = UType.parse(APContext.typeElement(NonNullPrism.PRISM_TYPE).asType());
      annotations.add(Map.entry(nonNull, "Map.of(\"message\",\"{avaje.NotNull.message}\")"));
    }

    return new ElementAnnotationContainer(uType, hasValid, annotations, typeUse1, typeUse2, crossParam);
  }

  private static List<Entry<UType, String>> annotations(Element element, UType uType, List<Entry<UType, String>> crossParam) {
    return Stream.concat(element.getAnnotationMirrors().stream(), uType.annotations().stream())
      .filter(a -> excludePlainValid(a, element))
      .filter(ElementAnnotationContainer::hasMetaConstraintAnnotation)
      .map(a -> {
        if (CrossParamConstraintPrism.isPresent(a.getAnnotationType().asElement())) {
          crossParam.add(
            Map.entry(
              UType.parse(a.getAnnotationType()),
              AnnotationUtil.annotationAttributeMap(a, element)));
          return null;
        }
        return a;
      })
      .filter(Objects::nonNull)
      .map(a -> checkType(element, uType, a))
      .map(a ->
        Map.entry(
          UType.parse(a.getAnnotationType()),
          AnnotationUtil.annotationAttributeMap(a, element)))
      .distinct()
      // valid annotation goes last
      .sorted(Comparator.comparing(
          e -> e.getKey().shortType(),
          Comparator.comparing("Valid"::equals)))
      .collect(toList());
  }

  private static AnnotationMirror checkType(Element element, UType uType, AnnotationMirror a) {
    ConstraintPrism.getOptionalOn(a.getAnnotationType().asElement())
      .map(ConstraintPrism::targets)
      .filter(l -> !l.isEmpty())
      .ifPresent(l -> {
        if (l.stream().noneMatch(t ->
          APContext.types().isAssignable(uType.mirror(), t)
            || "java.util.Optional".equals(uType.mainType())
            && APContext.types().isAssignable(uType.param0().mirror(), t))) {
          APContext.logError(
            element,
            "@%s cannot be used on %s",
            ProcessorUtils.shortType(a.getAnnotationType().toString()),
            uType.shortWithoutAnnotations());
        }
      });
    return a;
  }

  /** Only include Valid with groups defined */
  private static boolean excludePlainValid(AnnotationMirror a, Element element) {
    return !ValidPrism.isInstance(a) || !ValidPrism.instance(a).groups().isEmpty() && !(element instanceof TypeElement);
  }

  private static List<Entry<UType, String>> typeUseFor(UType uType, Element element) {
    return Optional.ofNullable(uType).map(UType::annotations).stream()
      .flatMap(List::stream)
      .filter(ElementAnnotationContainer::hasMetaConstraintAnnotation)
      .map(a -> checkType(element, uType, a))
      .map(a ->
        Map.entry(
          UType.parse(a.getAnnotationType()),
          AnnotationUtil.annotationAttributeMap(a, element)))
      .toList();
  }

  static boolean hasMetaConstraintAnnotation(AnnotationMirror m) {
    return hasMetaConstraintAnnotation(m.getAnnotationType().asElement())
        || ValidPrism.isInstance(m);
  }

  static boolean hasMetaConstraintAnnotation(Element element) {
    return ConstraintPrism.isPresent(element);
  }

  public void addImports(Set<String> importTypes) {
    importTypes.addAll(genericType.importTypes());
    annotations.forEach(t -> importTypes.addAll(t.getKey().importTypes()));
    typeUse1.forEach(t -> importTypes.addAll(t.getKey().importTypes()));
    typeUse2.forEach(t -> importTypes.addAll(t.getKey().importTypes()));
  }

  boolean isEmpty() {
    return annotations.isEmpty() && typeUse1.isEmpty() && typeUse2.isEmpty();
  }

  boolean supportsPrimitiveValidation() {
    for (final var entry : annotations) {
      var validationAnnotation = entry.getKey();
      ConstraintPrism.getOptionalOn(typeElement(validationAnnotation.full()))
        .ifPresent(p -> {
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
