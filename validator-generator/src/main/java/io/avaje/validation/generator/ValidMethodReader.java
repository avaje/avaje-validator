package io.avaje.validation.generator;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

final class ValidMethodReader {

  private final ExecutableElement methodElement;
  private final String type;
  private final Set<String> importTypes = new TreeSet<>();
  private final List<? extends VariableElement> params;
  private final List<ElementAnnotationContainer> paramAnnotations;

  ValidMethodReader(ExecutableElement element) {
    this.methodElement = element;
    this.type = element.getEnclosingElement().toString();
    this.params = element.getParameters();
    importTypes.add(type);
    importTypes.add("java.util.List");
    importTypes.add(Constants.COMPONENT);
    importTypes.add("java.util.Set");
    importTypes.add("java.util.Map");
    importTypes.add("io.avaje.validation.inject.aspect.MethodAdapterProvider");
    importTypes.add("io.avaje.validation.adapter.ValidationAdapter");
    importTypes.add("io.avaje.validation.adapter.ValidationContext");
    importTypes.add("io.avaje.validation.spi.Generated");
    importTypes.add("java.lang.reflect.Method");
    paramAnnotations = params.stream().map(ElementAnnotationContainer::create).toList();
  }

  public String shortName() {
    return methodElement.getSimpleName().toString();
  }

  private Set<String> importTypes() {
    if (Util.validImportType(type)) {
      importTypes.add(type);
    }

    paramAnnotations.forEach(a -> a.addImports(importTypes));
    return importTypes;
  }

  public void writeImports(Append writer, boolean writeAspect) {
    if (writeAspect) {
      importTypes.add("io.avaje.validation.inject.aspect.AOPMethodValidator");
    }
    for (final String importType : importTypes()) {
      if (Util.validImportType(importType)) {
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
            .map(p -> Util.trimAnnotations(p.asType().toString()))
            .map(GenericType::parse)
            .map(GenericType::topType)
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
  public List<ValidationAdapter<Object>> paramAdapters(ValidationContext ctx) {
""");
    writer.append("    return List.of(");
    final var size = paramAnnotations.size();
    for (int i = 0; i < paramAnnotations.size(); i++) {
      AdapterHelper.writeAdapterWithValues(writer, paramAnnotations.get(i), "\n        ", "Object");
      if (i + 1 != size) {
        writer.append(",");
      }
    }

    writer.append(");").eol();
    writer.append("  }").eol();
  }

  public ExecutableElement getBeanType() {
    return methodElement;
  }
}
