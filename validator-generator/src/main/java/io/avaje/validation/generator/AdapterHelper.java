package io.avaje.validation.generator;

import java.util.Map;

import static io.avaje.validation.generator.ProcessingContext.isAssignable2Interface;

final class AdapterHelper {

  private final Append writer;
  private final ElementAnnotationContainer elementAnnotations;
  private final String indent;
  private final String type;
  private final GenericType topType;
  private final GenericType genericType;
  private final boolean classLevel;
  private final boolean crossParam;
  private boolean usePrimitiveValidation;

  AdapterHelper(Append writer, ElementAnnotationContainer elementAnnotations, String indent) {
    this(writer, elementAnnotations, indent,"Object", null, false, false);
  }

  AdapterHelper(
      Append writer,
      ElementAnnotationContainer elementAnnotations,
      String indent,
      String type,
      GenericType topType,
      boolean classLevel) {
    this(writer, elementAnnotations, indent, type, topType, classLevel, false);
  }

  AdapterHelper(
      Append writer,
      ElementAnnotationContainer elementAnnotations,
      String indent,
      String type,
      GenericType topType,
      boolean classLevel,
      boolean crossParam) {
    this.writer = writer;
    this.elementAnnotations = elementAnnotations;
    this.indent = indent;
    this.type = type;
    this.topType = topType;
    this.genericType = elementAnnotations.genericType();
    this.classLevel = classLevel;
    this.crossParam = crossParam;
  }

  AdapterHelper usePrimitiveValidation(boolean usePrimitiveValidation) {
    this.usePrimitiveValidation = usePrimitiveValidation;
    return this;
  }

  void write() {
    final var typeUse1 = elementAnnotations.typeUse1();
    final var typeUse2 = elementAnnotations.typeUse2();
    final var hasValid = elementAnnotations.hasValid();
    writeFirst(crossParam ? elementAnnotations.crossParam() : elementAnnotations.annotations());
    if (crossParam) {
      return;
    }
    if (usePrimitiveValidation) {
      writer.eol().append("%s    .primitive()", indent);
      return;
    }

    if (!typeUse1.isEmpty() && (isAssignable2Interface(genericType.topType(), "java.lang.Iterable"))) {
      writer.eol().append("%s    .list()", indent);
      writeTypeUse(genericType.firstParamType(), typeUse1);

    } else if (isTopTypeIterable()) {
      writer.eol().append("%s    .list()", indent);
      if (hasValid) {
        // cascade validate
        writer.eol().append("%s    .andThenMulti(ctx.adapter(%s.class))", indent, Util.shortName(topType.firstParamType()));
      }

    } else if (isMapType(typeUse1, typeUse2)) {
      writer.eol().append("%s    .mapKeys()", indent);
      writeTypeUse(genericType.firstParamType(), typeUse1);

      writer.eol().append("%s    .mapValues()", indent);
      writeTypeUse(genericType.secondParamType(), typeUse2, false);

    } else if (hasValid && genericType.topType().contains("[]")) {
      writer.eol().append("%s    .array()", indent);
      writer.eol().append("%s    .andThenMulti(ctx.adapter(%s.class))", indent, topType.shortNameMinusArray());

    } else if (hasValid) {
      if (!classLevel) {
        writer.eol().append("%s    .andThen(ctx.adapter(%s.class))", indent, Util.shortName(genericType.topType()));
      }

    } else if (genericType.topType().contains("java.util.Optional")) {
      writer.eol().append("%s    .optional()", indent);
    }
  }

  private void writeFirst(Map<GenericType, String> annotations) {
    boolean first = true;
    for (final var a : annotations.entrySet()) {
      if (first) {
        writer.append("%sctx.<%s>adapter(%s.class, %s)", indent, type, a.getKey().shortName(), a.getValue());
        first = false;
        continue;
      }
      writer.eol().append("%s    .andThen(ctx.adapter(%s.class,%s))", indent, a.getKey().shortName(), a.getValue());
    }
    if (annotations.isEmpty()) {
      writer.append("%sctx.<%s>noop()", indent, type);
    }
  }

  private boolean isMapType(Map<GenericType, String> typeUse1, Map<GenericType, String> typeUse2) {
    return (!typeUse1.isEmpty() || !typeUse2.isEmpty())
      && "java.util.Map".equals(genericType.topType());
  }

  private boolean isTopTypeIterable() {
    return topType != null && isAssignable2Interface(topType.topType(), "java.lang.Iterable");
  }

  private void writeTypeUse(String firstParamType, Map<GenericType, String> typeUse12) {
    writeTypeUse(firstParamType, typeUse12, true);
  }

  private void writeTypeUse(String paramType, Map<GenericType, String> typeUseMap, boolean keys) {
    for (final var a : typeUseMap.entrySet()) {
      if (Constants.VALID_ANNOTATIONS.contains(a.getKey().topType())) {
        continue;
      }
      final var k = a.getKey().shortName();
      final var v = a.getValue();
      writer.eol().append("%s    .andThenMulti(ctx.adapter(%s.class,%s))", indent, k, v);
    }

    if (!Util.isBasicType(paramType)
        && typeUseMap.keySet().stream()
            .map(GenericType::topType)
            .anyMatch(Constants.VALID_ANNOTATIONS::contains)) {

      writer
          .eol()
          .append(
              "%s    .andThenMulti(ctx.adapter(%s.class))",
              indent,
              Util.shortName(keys ? genericType.firstParamType() : genericType.secondParamType()));
    }
  }
}
