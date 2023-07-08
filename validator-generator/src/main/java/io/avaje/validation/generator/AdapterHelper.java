package io.avaje.validation.generator;

import java.util.Map;

public class AdapterHelper {

  static void writeAdapterWithValues(
      Append writer, ElementAnnotationContainer elementAnnotations, String indent, String type) {
    boolean first = true;
    final var annotations = elementAnnotations.annotations();
    final var genericType = elementAnnotations.genericType();
    final var typeUse1 = elementAnnotations.typeUse1();
    final var typeUse2 = elementAnnotations.typeUse2();
    final var hasValid = elementAnnotations.hasValid();
    for (final var a : annotations.entrySet()) {
      if (first) {
        writer.append(
            "%sctx.<%s>adapter(%s.class, %s)", indent, type, a.getKey().shortName(), a.getValue());
        first = false;
        continue;
      }
      writer
          .eol()
          .append(
              "%s    .andThen(ctx.adapter(%s.class,%s))",
              indent, a.getKey().shortName(), a.getValue());
    }

    if (annotations.isEmpty()) {
      writer.append("%sctx.<%s>noop()", indent, type);
    }

    if (!typeUse1.isEmpty()
        && ("java.util.List".equals(genericType.topType())
            || "java.util.Set".equals(genericType.topType()))) {
      writer.eol().append("%s    .list()", indent);
      final var t = genericType.firstParamType();
      writeTypeUse(writer, indent, t, typeUse1, genericType);

    } else if ((!typeUse1.isEmpty() || !typeUse2.isEmpty())
        && "java.util.Map".equals(genericType.topType())) {

      writer.eol().append("%s    .mapKeys()", indent);
      writeTypeUse(writer, indent, genericType.firstParamType(), typeUse1, genericType);

      writer.eol().append("%s    .mapValues()", indent);
      writeTypeUse(writer, indent, genericType.secondParamType(), typeUse2, false, genericType);

    } else if (genericType.topType().contains("[]") && hasValid) {

      writer.eol().append("%s    .array()", indent);
      writeTypeUse(writer, indent, genericType.firstParamType(), typeUse1, genericType);
    } else if (hasValid) {
      writer
          .eol()
          .append(
              "%s    .andThen(ctx.adapter(%s.class))",
              indent, Util.shortName(genericType.topType()));
    } else if (genericType.topType().contains("java.util.Optional")) {
      writer.eol().append("%s    .optional()", indent);
    }
  }

  private static void writeTypeUse(
      Append writer,
      String indent,
      String firstParamType,
      Map<GenericType, String> typeUse12,
      GenericType genericType) {
    writeTypeUse(writer, indent, firstParamType, typeUse12, true, genericType);
  }

  private static void writeTypeUse(
      Append writer,
      String indent,
      String paramType,
      Map<GenericType, String> typeUseMap,
      boolean keys,
      GenericType genericType) {

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
