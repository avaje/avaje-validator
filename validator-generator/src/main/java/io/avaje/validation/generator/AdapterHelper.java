package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.isAssignable;

import java.util.List;
import java.util.Map.Entry;

final class AdapterHelper {

  private static final String OPTIONAL = "java.util.Optional";
  private final Append writer;
  private final ElementAnnotationContainer elementAnnotations;
  private final String indent;
  private final String type;
  private final UType mainType;
  private final UType genericType;
  private final boolean classLevel;
  private final boolean crossParam;
  private boolean usePrimitiveValidation;
  private String recursiveType;

  AdapterHelper(Append writer, ElementAnnotationContainer elementAnnotations, String indent) {
    this(writer, elementAnnotations, indent, "Object", null, false, false);
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

    if (!typeUse1.isEmpty() && isAssignable(genericType.mainType(), "java.lang.Iterable")) {
      writer.eol().append("%s    .list()", indent);
      writeTypeUse(genericType.param0(), typeUse1);

    } else if (isTopTypeIterable()) {
      writer.eol().append("%s    .list()", indent);
      // cascade validate
      if (hasValid) {
        if (mainType.param0().fullWithoutAnnotations().equals(recursiveType)) {
          // cascade validate
          writer.eol().append("%s    .andThenMulti(this)", indent, mainType.param0().shortType());
        } else {
          // cascade validate
          writer.eol().append("%s    .andThenMulti(ctx.adapter(%s.class))", indent, mainType.param0().shortType());
        }
      }

    } else if (isMapType(typeUse1, typeUse2)) {
      writer.eol().append("%s    .mapKeys()", indent);
      writeTypeUse(genericType.param0(), typeUse1);
      writer.eol().append("%s    .mapValues()", indent);
      writeTypeUse(genericType.param1(), typeUse2, false);

    } else if (hasValid && genericType.mainType().contains("[]")) {
      writer.eol().append("%s    .array()", indent);
      if (genericType.mainType().replace("[]", "").equals(recursiveType)) {
        writer.eol().append("%s    .andThenMulti(this)", indent);
      } else {
        writer.eol().append("%s    .andThenMulti(ctx.adapter(%s.class))",
          indent, mainType.shortWithoutAnnotations().replace("[]", ""));
      }

    } else if (OPTIONAL.equals(genericType.mainType())) {
      // cascade validate
      if (hasValid) {
        if (mainType.param0().fullWithoutAnnotations().equals(recursiveType)) {
          // cascade validate
          writer.eol().append("%s    .andThen(this)", indent, mainType.param0().shortWithoutAnnotations());
        } else {
          // cascade validate
          writer.eol().append("%s    .andThen(ctx.adapter(%s.class))", indent, mainType.param0().shortWithoutAnnotations());
        }
      }
      writer.eol().append("%s    .optional()", indent);
    } else if (genericType.mainType().contains(OPTIONAL)) {
      writer.eol().append("%s    .primitiveOptional()", indent);
    } else if (hasValid && !classLevel) {
      if (genericType.mainType().equals(recursiveType)) {
        writer.eol().append("%s    .andThen(this)", indent);
      } else {
        writer.eol().append("%s    .andThen(ctx.adapter(%s.class))", indent, genericType.shortWithoutAnnotations());
      }
    }
  }

  private void writeFirst(List<Entry<UType, String>> annotations) {
    boolean first = true;

    var type =
      OPTIONAL.equals(genericType.mainType())
        ? genericType.param0().shortWithoutAnnotations()
        : this.type;

    for (final var a : annotations) {
      if (first) {
        writer.append("%sctx.<%s>adapter(%s.class, %s)", indent, type, a.getKey().shortWithoutAnnotations(), a.getValue());
        first = false;
        continue;
      }
      writer.eol().append("%s    .andThen(ctx.adapter(%s.class, %s))", indent, a.getKey().shortWithoutAnnotations(), a.getValue());
    }
    if (annotations.isEmpty()) {
      writer.append("%sctx.<%s>noop()", indent, type);
    }
  }

  private boolean isMapType(List<Entry<UType, String>> typeUse1, List<Entry<UType, String>> typeUse2) {
    return (!typeUse1.isEmpty() || !typeUse2.isEmpty())
      && "java.util.Map".equals(genericType.mainType());
  }

  private boolean isTopTypeIterable() {
    return mainType != null && isAssignable(mainType.mainType(), "java.lang.Iterable");
  }

  private void writeTypeUse(UType uType, List<Entry<UType, String>> typeUse1) {
    writeTypeUse(uType, typeUse1, true);
  }

  private void writeTypeUse(UType uType, List<Entry<UType, String>> typeUse1, boolean keys) {
    for (final var a : typeUse1) {

      if (Constants.VALID_ANNOTATIONS.contains(a.getKey().mainType())) {
        continue;
      }
      final var k = a.getKey().shortType();
      final var v = a.getValue();
      writer.eol().append("%s    .andThenMulti(ctx.adapter(%s.class, %s))", indent, k, v);
    }

    if (!Util.isBasicType(uType.fullWithoutAnnotations())
        && typeUse1.stream().map(Entry::getKey)
            .map(UType::mainType)
            .anyMatch(Constants.VALID_ANNOTATIONS::contains)) {
      var typeUse = keys ? genericType.param0() : genericType.param1();
      writer
          .eol()
          .append("%s    .andThenMulti(ctx.adapter(%s.class))", indent, typeUse.shortWithoutAnnotations());
    }
  }

  void withEnclosingType(UType recursive) {
    this.recursiveType = recursive.fullWithoutAnnotations();
  }
}
