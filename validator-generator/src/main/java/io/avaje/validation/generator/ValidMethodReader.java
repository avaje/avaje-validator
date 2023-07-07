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
      writeAdapters(writer, paramAnnotations.get(i));
      if (i + 1 != size) {
        writer.append(",");
      }
    }

    writer.append(");").eol();
    writer.append("  }").eol();
  }

  private void writeAdapters(Append writer, final ElementAnnotationContainer params) {
    final var genericType = params.genericType();
    final var paramAnnotations = params.annotations();
    final var typeUse1 = params.typeUse1();
    final var typeUse2 = params.typeUse2();
    final boolean hasValid = params.hasValid();

    boolean first = true;
    for (final var a : paramAnnotations.entrySet()) {
      if (first) {
        writer
            .eol()
            .append(
                "        ctx.<Object>adapter(%s.class, %s)", a.getKey().shortName(), a.getValue());
        first = false;
        continue;
      }
      writer
          .eol()
          .append(
              "            .andThen(ctx.adapter(%s.class,%s))",
              a.getKey().shortName(), a.getValue());
    }

    if (paramAnnotations.isEmpty()) {
      writer
          .eol()
          .append("        ctx.<Object>noop()", PrimitiveUtil.wrap(genericType.shortType()));
    }

    if (!typeUse1.isEmpty()
        && ("java.util.List".equals(genericType.topType())
            || "java.util.Set".equals(genericType.topType()))) {
      writer.eol().append("            .list()");
      final var t = genericType.firstParamType();
      writeTypeUse(writer, t, genericType, typeUse1);

    } else if ((!typeUse1.isEmpty() || !typeUse2.isEmpty())
        && "java.util.Map".equals(genericType.topType())) {

      writer.eol().append("            .mapKeys()");
      writeTypeUse(writer, genericType.firstParamType(), genericType, typeUse1);

      writer.eol().append("            .mapValues()");
      writeTypeUse(writer, genericType.secondParamType(), genericType, typeUse2, false);

    } else if (genericType.topType().contains("[]") && hasValid) {

      writer.eol().append("            .array()");
      writeTypeUse(writer, genericType.firstParamType(), genericType, typeUse1);
    } else if (hasValid) {
      writer
          .eol()
          .append(
              "            .andThen(ctx.adapter(%s.class))", Util.shortName(genericType.topType()));
    }
  }

  private void writeTypeUse(
      Append writer,
      String firstParamType,
      GenericType genericType,
      Map<GenericType, String> typeUse12) {
    writeTypeUse(writer, firstParamType, genericType, typeUse12, true);
  }

  private void writeTypeUse(
      Append writer,
      String firstParamType,
      GenericType genericType,
      Map<GenericType, String> typeUseMap,
      boolean keys) {

    for (final var a : typeUseMap.entrySet()) {

      if (Constants.VALID_ANNOTATIONS.contains(a.getKey().topType())) {
        continue;
      }
      final var k = a.getKey().shortName();
      final var v = a.getValue();
      writer.eol().append("            .andThenMulti(ctx.adapter(%s.class,%s))", k, v);
    }

    if (!Util.isBasicType(firstParamType)
        && typeUseMap.keySet().stream()
            .map(GenericType::topType)
            .anyMatch(Constants.VALID_ANNOTATIONS::contains)) {

      writer
          .eol()
          .append(
              "            .andThenMulti(ctx.adapter(%s.class))",
              Util.shortName(keys ? genericType.firstParamType() : genericType.secondParamType()));
    }
  }

  public ExecutableElement getBeanType() {
    return methodElement;
  }
}
