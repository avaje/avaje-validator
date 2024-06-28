package io.avaje.validation.generator;

import java.util.Map;

import static io.avaje.validation.generator.APContext.isAssignable;

final class AdapterHelper {

  private final Append writer;
  private final ElementAnnotationContainer elementAnnotations;
  private final String indent;
  private final String type;
  private final UType mainType;
  private final UType genericType;
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
      UType mainType,
      boolean classLevel) {
    this(writer, elementAnnotations, indent, type, mainType, classLevel, false);
  }

  AdapterHelper(
      Append writer,
      ElementAnnotationContainer elementAnnotations,
      String indent,
      String type,
      UType mainType,
      boolean classLevel,
      boolean crossParam) {
    this.writer = writer;
    this.elementAnnotations = elementAnnotations;
    this.indent = indent;
    this.type = type;
    this.mainType = mainType;
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

    if (!typeUse1.isEmpty() && (isAssignable(genericType.mainType(), "java.lang.Iterable"))) {
      writer.eol().append("%s    .list()", indent);
      writeTypeUse(genericType.param0(), typeUse1);

    } else if (isTopTypeIterable()) {
      writer.eol().append("%s    .list()", indent);
      if (hasValid) {
        // cascade validate
        writer.eol().append("%s    .andThenMulti(ctx.adapter(%s.class))", indent, mainType.param0().shortType());
      }

    } else if (isMapType(typeUse1, typeUse2)) {
      writer.eol().append("%s    .mapKeys()", indent);
      writeTypeUse(genericType.param0(), typeUse1);

      writer.eol().append("%s    .mapValues()", indent);
      writeTypeUse(genericType.param1(), typeUse2, false);

    } else if (hasValid && genericType.mainType().contains("[]")) {
      writer.eol().append("%s    .array()", indent);
      writer.eol().append("%s    .andThenMulti(ctx.adapter(%s.class))", indent, mainType.shortWithoutAnnotations().replace("[]",""));

    } else if (hasValid) {
      if (!classLevel) {
        writer.eol().append("%s    .andThen(ctx.adapter(%s.class))", indent, genericType.shortWithoutAnnotations());
      }

    } else if (genericType.mainType().contains("java.util.Optional")) {
      writer.eol().append("%s    .optional()", indent);
    }
  }

  private void writeFirst(Map<UType, String> annotations) {
    boolean first = true;
    for (final var a : annotations.entrySet()) {
      if (first) {
        writer.append("%sctx.<%s>adapter(%s.class, %s)", indent, type, a.getKey().shortWithoutAnnotations(), a.getValue());
        first = false;
        continue;
      }
      writer.eol().append("%s    .andThen(ctx.adapter(%s.class,%s))", indent, a.getKey().shortWithoutAnnotations(), a.getValue());
    }
    if (annotations.isEmpty()) {
      writer.append("%sctx.<%s>noop()", indent, type);
    }
  }

  private boolean isMapType(Map<UType, String> typeUse1, Map<UType, String> typeUse2) {
    return (!typeUse1.isEmpty() || !typeUse2.isEmpty())
      && "java.util.Map".equals(genericType.mainType());
  }

  private boolean isTopTypeIterable() {
    return mainType != null && isAssignable(mainType.mainType(), "java.lang.Iterable");
  }

  private void writeTypeUse(UType uType, Map<UType, String> typeUse12) {
    writeTypeUse(uType, typeUse12, true);
  }

  private void writeTypeUse(UType uType, Map<UType, String> typeUseMap, boolean keys) {
    for (final var a : typeUseMap.entrySet()) {

      if (Constants.VALID_ANNOTATIONS.contains(a.getKey().mainType())) {
        continue;
      }
      final var k = a.getKey().shortType();
      final var v = a.getValue();
      writer.eol().append("%s    .andThenMulti(ctx.adapter(%s.class,%s))", indent, k, v);
    }

    if (!Util.isBasicType(uType.fullWithoutAnnotations())
        && typeUseMap.keySet().stream()
            .map(UType::mainType)
            .anyMatch(Constants.VALID_ANNOTATIONS::contains)) {
      var typeUse = keys ? genericType.param0() : genericType.param1();
      writer
          .eol()
          .append("%s    .andThenMulti(ctx.adapter(%s.class))", indent, typeUse.shortWithoutAnnotations());
    }
  }
}
