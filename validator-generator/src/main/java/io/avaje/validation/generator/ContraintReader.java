package io.avaje.validation.generator;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;

final class ContraintReader implements BeanReader {

  private final TypeElement beanType;
  private final String type;
  private final Set<String> importTypes = new TreeSet<>();
  private final Map<UType, String> annotations;

  ContraintReader(TypeElement element) {
    this.beanType = element;
    this.type = element.getQualifiedName().toString();
    importTypes.add(type);
    importTypes.add("java.util.List");
    importTypes.add("java.util.Set");
    importTypes.add("java.util.Map");
    importTypes.add("io.avaje.validation.adapter.ConstraintAdapter");
    importTypes.add("io.avaje.validation.adapter.ValidationAdapter");
    importTypes.add("io.avaje.validation.adapter.ValidationRequest");
    importTypes.add("io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest");
    importTypes.add("io.avaje.validation.spi.Generated");

    this.annotations =
        element.getAnnotationMirrors().stream()
            .filter(a -> ConstraintPrism.isPresent(a.getAnnotationType().asElement()))
            .flatMap(m -> expand(m, new ArrayList<>()).stream())
            .collect(
                toMap(
                    a -> UType.parse(a.getAnnotationType()),
                    a -> AnnotationUtil.annotationAttributeMap(a, element)));
  }

  private List<AnnotationMirror> expand(AnnotationMirror m, List<AnnotationMirror> mirrors) {
    mirrors.add(m);

    m.getAnnotationType().getAnnotationMirrors().stream()
        .filter(a -> ConstraintPrism.isPresent(a.getAnnotationType().asElement()))
        .forEach(mirrors::add);

    return mirrors;
  }

  @Override
  public int genericTypeParamsCount() {
    return 0;
  }

  @Override
  public String contraintTarget() {
    return Util.shortName(type);
  }

  @Override
  public String toString() {
    return beanType.toString();
  }

  @Override
  public String shortName() {
    return "Object";
  }

  @Override
  public TypeElement getBeanType() {
    return beanType;
  }

  @Override
  public boolean nonAccessibleField() {
    return false;
  }

  @Override
  public boolean hasValidationAnnotation() {
    return false;
  }

  @Override
  public void read() {}

  private Set<String> importTypes() {
    importTypes.add(type);

    annotations.keySet().forEach(t -> importTypes.addAll(t.importTypes()));
    return importTypes;
  }

  @Override
  public void writeImports(Append writer, String adapterPackage) {
    for (final String importType : importTypes()) {
      if (Util.validImportType(importType, adapterPackage)) {
        writer.append("import %s;", importType).eol();
      }
    }
    writer.eol();
  }

  @Override
  public void cascadeTypes(Set<String> types) {}

  @Override
  public void writeFields(Append writer) {
    writer.append("  private final ValidationAdapter<Object> adapter;").eol().eol();
  }

  @Override
  public void writeConstructor(Append writer) {

    writer.append(
        """
    final var message = req.message().template();
    final var ctx = req.ctx();
    final var groups = req.groups();
    this.adapter =
""");

    boolean first = true;
    for (final var a : new ArrayList<>(annotations.entrySet())) {
      if (first) {
        writer.append("        ctx.adapter(%s.class, groups, message, %s)", a.getKey().shortWithoutAnnotations(), a.getValue());
        first = false;
        continue;
      }
      writer
          .eol()
          .append(
              "            .andThen(ctx.adapter(%s.class, groups, message, %s))",
              a.getKey().shortWithoutAnnotations(), a.getValue());
    }
    writer.append(";").eol();
  }

  @Override
  public void writeValidatorMethod(Append writer) {
    writer.eol();
    writer
        .append(
            """
  @Override
  public boolean validate(Object value, ValidationRequest req, String propertyName) {

    return adapter.validate(value, req, propertyName);
  }
""")
        .eol();
  }
}
