package io.avaje.validation.generator;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

final class AnnotationUtil {
  private AnnotationUtil() {}

  public static String getAnnotationAttributMap(AnnotationMirror annotationMirror) {
    final StringBuilder sb = new StringBuilder("Map.of(");
    boolean first = true;
    final var patternOp = PatternPrism.isInstance(annotationMirror);

    if (patternOp.isPresent()) {
      patternOp.ifPresent(p -> pattern(sb, p));
      return sb.toString();
    }

    for (final ExecutableElement member :
        ElementFilter.methodsIn(
            annotationMirror.getAnnotationType().asElement().getEnclosedElements())) {

      final var value =
          Optional.<AnnotationValue>ofNullable(annotationMirror.getElementValues().get(member))
              .orElseGet(member::getDefaultValue);
      if (value == null) {
        continue;
      }
      if (!first) {
        sb.append(", ");
      }
      sb.append("\"" + member.getSimpleName() + "\"").append(",");
      writeVal(sb, value);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  private static void pattern(StringBuilder sb, PatternPrism prism) {
    if (prism.regexp() != null) {
      sb.append("\"regexp\",\"" + prism.regexp() + "\"");
    }

    if (prism.message() != null) {
      sb.append(", \"message\",\"" + prism.message() + "\"");
    }
    if (!prism.flags().isEmpty()) {
      sb.append(", \"flags\",List.of(" + prism.flags().stream().collect(joining(", ")) + ")");
    }

    sb.append(")");
  }

  private static void writeVal(final StringBuilder sb, final AnnotationValue annotationValue) {
    final var value = annotationValue.getValue();
    // handle array values
    if (value instanceof List) {
      sb.append("List.of(");
      boolean first = true;

      for (final AnnotationValue listValue : (List<AnnotationValue>) value) {

        if (!first) {
          sb.append(", ");
        }

        writeVal(sb, listValue);
        first = false;
      }
      sb.append(")");
      // Handle enum values
    } else if (value instanceof final VariableElement element) {
      sb.append(element.asType().toString() + "." + element.toString());
      // handle annotation values
    } else if (value instanceof AnnotationMirror) {

      sb.append("\"Annotation Parameters Not Supported\"");

    } else {
      sb.append(annotationValue.toString());
    }
  }
}
