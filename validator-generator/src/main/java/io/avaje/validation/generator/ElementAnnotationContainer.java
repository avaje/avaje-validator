package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.isAssignable;
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
import javax.lang.model.type.TypeMirror;

record ElementAnnotationContainer(
    UType genericType,
    boolean hasValid,
    List<Entry<UType, String>> annotations,
    List<Entry<UType, String>> typeUse1,
    List<Entry<UType, String>> typeUse2,
    List<Entry<UType, String>> crossParam,
    NestedElement nested1,
    NestedElement nested2) {

  /**
   * Captures a container element type (the immediate type argument of a List/Set/Map) that is
   * itself a container (List/Set/Map), so that container-element constraints can be validated
   * recursively.
   */
  record NestedElement(
      UType type,
      List<Entry<UType, String>> direct0,
      List<Entry<UType, String>> direct1,
      NestedElement child0,
      NestedElement child1,
      boolean map) {}

  static ElementAnnotationContainer create(Element element, TypeMirror resolvedType) {
    return create(element, UType.parse(resolvedType));
  }

  static ElementAnnotationContainer create(Element element) {
    UType uType;
    if (element instanceof final ExecutableElement executableElement) {
      uType = UType.parse(executableElement.getReturnType());
    } else {
      uType = UType.parse(element.asType());
    }
    return create(element, uType);
  }

  private static ElementAnnotationContainer create(Element element, UType uType) {
    final var hasValid =
      ValidPrism.isPresent(element)
        || uType.annotations().stream().anyMatch(ValidPrism::isInstance);

    List<Entry<UType, String>> typeUse1 = typeUseFor(uType.param0(), element);
    List<Entry<UType, String>> typeUse2 = typeUseFor(uType.param1(), element);
    final boolean topIsContainer = isContainerType(uType);
    final boolean isMap = "java.util.Map".equals(uType.mainType());
    final NestedElement nested1 = topIsContainer ? buildNested(uType.param0(), element) : null;
    final NestedElement nested2 =
        topIsContainer && isMap ? buildNested(uType.param1(), element) : null;

    final List<Entry<UType, String>> crossParam = new ArrayList<>();
    final var annotations = annotations(element, uType, crossParam);

    if (Util.isNonNullable(element)) {
      var nonNull = UType.parse(APContext.typeElement(NonNullPrism.PRISM_TYPE).asType());
      annotations.add(Map.entry(nonNull, "Map.of(\"message\",\"{avaje.NotNull.message}\")"));
    }

    return new ElementAnnotationContainer(
      uType, hasValid, annotations, typeUse1, typeUse2, crossParam, nested1, nested2);
  }

  static boolean isContainerType(UType type) {
    if (type == null) {
      return false;
    }
    final String mainType = type.mainType();
    return "java.util.Map".equals(mainType) || isAssignable(mainType, "java.lang.Iterable");
  }

  /**
   * Recursively build the nested container structure (if any) found within the given type
   * argument. Returns {@code null} when {@code type} is not itself a container, or is a
   * container with no constraints (direct or nested) on its own type arguments.
   */
  private static NestedElement buildNested(UType type, Element element) {
    if (!isContainerType(type)) {
      return null;
    }
    final boolean map = "java.util.Map".equals(type.mainType());
    final UType param0 = type.param0();
    final UType param1 = map ? type.param1() : null;

    final List<Entry<UType, String>> direct0 = typeUseFor(param0, element);
    final List<Entry<UType, String>> direct1 = map ? typeUseFor(param1, element) : List.of();
    final NestedElement child0 = buildNested(param0, element);
    final NestedElement child1 = map ? buildNested(param1, element) : null;

    if (direct0.isEmpty() && direct1.isEmpty() && child0 == null && child1 == null) {
      return null;
    }
    return new NestedElement(type, direct0, direct1, child0, child1, map);
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
    crossParam.forEach(t -> importTypes.addAll(t.getKey().importTypes()));
    addNestedImports(nested1, importTypes);
    addNestedImports(nested2, importTypes);
  }

  private static void addNestedImports(NestedElement nested, Set<String> importTypes) {
    if (nested == null) {
      return;
    }
    importTypes.addAll(nested.type().importTypes());
    nested.direct0().forEach(t -> importTypes.addAll(t.getKey().importTypes()));
    nested.direct1().forEach(t -> importTypes.addAll(t.getKey().importTypes()));
    addNestedImports(nested.child0(), importTypes);
    addNestedImports(nested.child1(), importTypes);
  }

  boolean isEmpty() {
    return annotations.isEmpty()
      && typeUse1.isEmpty()
      && typeUse2.isEmpty()
      && nested1 == null
      && nested2 == null;
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
