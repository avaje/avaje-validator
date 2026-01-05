package io.avaje.validation.generator;

import static java.util.stream.Collectors.joining;
import static io.avaje.validation.generator.ProcessingContext.diAnnotation;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

final class ValidMethodReader {

  private final ExecutableElement methodElement;
  private final String type;
  private final Set<String> importTypes = new TreeSet<>();
  private final List<? extends VariableElement> params;
  private final List<ElementAnnotationContainer> paramAnnotations;
  private final ElementAnnotationContainer returnElementAnnotation;

  ValidMethodReader(ExecutableElement element) {
    this.methodElement = element;
    this.type = element.getEnclosingElement().toString();
    this.params = element.getParameters();
    importTypes.add(type);
    importTypes.add("java.util.List");
    importTypes.add("java.util.Set");
    importTypes.add("java.util.Map");
    importTypes.add("io.avaje.validation.adapter.MethodAdapterProvider");
    importTypes.add("io.avaje.validation.adapter.ValidationAdapter");
    importTypes.add("io.avaje.validation.adapter.ValidationContext");
    importTypes.add("io.avaje.validation.spi.Generated");
    importTypes.add("java.lang.reflect.Method");
    final var diAnnotation = diAnnotation();
    importTypes.add(diAnnotation);
    importTypes.add(diAnnotation.contains("javax") ? "javax.inject.Named" : "jakarta.inject.Named");
    paramAnnotations = params.stream().map(ElementAnnotationContainer::create).toList();
    returnElementAnnotation = ElementAnnotationContainer.create(element);
  }

  public String shortName() {
    return methodElement.getSimpleName().toString();
  }

  private Set<String> importTypes() {
    importTypes.add(type);
    paramAnnotations.forEach(a -> a.addImports(importTypes));
    returnElementAnnotation.addImports(importTypes);
    return importTypes;
  }

  public void writeImports(Append writer, String packageName) {
    for (final String importType : importTypes()) {
      if (Util.validImportType(importType, packageName)) {
        writer.append("import %s;", importType).eol();
      }
    }
    writer.eol();
  }

  public void writeValidatorMethod(Append writer) {
    writer.append("""
  @Override
  public Method method() throws Exception {
""");

    writer.append(
        "    return %s.class.getDeclaredMethod(\"%s\"",
        Util.shortName(type), methodElement.getSimpleName());
    final var paramClasses =
        params.stream()
            .map(VariableElement::asType)
            .map(UType::parse)
            .map(UType::mainType)
            .map(Util::shortName)
            .map(s -> s + ".class")
            .collect(joining(","))
            .transform(s -> s.endsWith(",") ? s.substring(0, s.length() - 1) : s)
            .transform(s -> s.isBlank() ? s : ", " + s);

    writer.append(paramClasses);

    writer.append(
"""
);
  }

  @Override
  public List<ValidationAdapter<?>> paramAdapters(ValidationContext ctx) {
""");
    writer.append("    return List.of(");
    final var size = paramAnnotations.size();
    for (int i = 0; i < paramAnnotations.size(); i++) {
      var container = paramAnnotations.get(i);
      var genericType = container.genericType();
      var type = genericType.kind() != TypeKind.DECLARED ? "Object" : genericType.shortWithoutAnnotations();
      new AdapterHelper(writer, container, "\n        ", type, genericType, false).write();

      if (i + 1 != size) {
        writer.append(",");
      }
    }

    writer.append(
        """
    );
      }

      @Override
      public ValidationAdapter<Object> returnAdapter(ValidationContext ctx) {
    """);
    writer.append("    return ");
    new AdapterHelper(writer, returnElementAnnotation, "").write();

    writer.append(
        """
        ;
          }

          @Override
          public ValidationAdapter<Object[]> crossParamAdapter(ValidationContext ctx) {
        """);
    writer.append("    return ");

    new AdapterHelper(writer, returnElementAnnotation, "", "Object[]", null, false, true).write();

    writer.append(";").eol();
    writer.append("  }").eol();
  }

  public ExecutableElement getBeanType() {
    return methodElement;
  }
}
