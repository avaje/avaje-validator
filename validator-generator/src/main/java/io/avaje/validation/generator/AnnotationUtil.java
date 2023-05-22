package io.avaje.validation.generator;

import java.util.*;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;

final class AnnotationUtil {

  interface Handler {
    String attributes(AnnotationMirror annotationMirror, Element element);
  }

  static final Handler defaultHandler = new StandardHandler();
  static final Map<String, Handler> handlers = new HashMap<>();
  static {
    final var pattern = new PatternHandler();
    handlers.put("avaje.Pattern", pattern);
    handlers.put("jakarta.validation.constraints.Pattern", pattern);

    Handler jakartaHandler = new JakartaHandler();
    handlers.put("jakarta.validation.constraints.NotBlank", jakartaHandler);
    handlers.put("jakarta.validation.constraints.Size", jakartaHandler);
  }

  private AnnotationUtil() {}

  static String annotationAttributeMap(AnnotationMirror annotationMirror) {
    final Element element = annotationMirror.getAnnotationType().asElement();
    final Handler handler = handlers.get(element.toString());
    return Objects.requireNonNullElse(handler, defaultHandler).attributes(annotationMirror, element);
  }

  static abstract class BaseHandler implements Handler {
    final StringBuilder sb = new StringBuilder("Map.of(");
    boolean first = true;

    @SuppressWarnings("unchecked")
    final void writeVal(final StringBuilder sb, final AnnotationValue annotationValue) {
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
        sb.append(element.asType().toString()).append(".").append(element);
        // handle annotation values
      } else if (value instanceof AnnotationMirror) {
        sb.append("\"Annotation Parameters Not Supported\"");
      } else {
        sb.append(annotationValue);
      }
    }
  }
  static class PatternHandler extends BaseHandler {

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element) {
      return new PatternHandler().writeAttributes(annotationMirror);
    }

    String writeAttributes(AnnotationMirror annotationMirror) {
      final var patternOp = PatternPrism.isInstance(annotationMirror);
      patternOp.ifPresent(p -> pattern(sb, p));
      return sb.toString();
    }
    private static void pattern(StringBuilder sb, PatternPrism prism) {
      if (prism.regexp() != null) {
        sb.append("\"regexp\",\"").append(prism.regexp()).append("\"");
      }
      if (prism.message() != null) {
        sb.append(", \"message\",\"").append(prism.message()).append("\"");
      }
      if (!prism.flags().isEmpty()) {
        sb.append(", \"flags\",List.of(").append(String.join(", ", prism.flags())).append(")");
      }
      sb.append(")");
    }
  }

  static class StandardHandler extends BaseHandler {

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element) {
      return new StandardHandler().writeAttributes(annotationMirror, element);
    }

    String writeAttributes(AnnotationMirror annotationMirror, Element element) {
      for (final ExecutableElement member : ElementFilter.methodsIn(element.getEnclosedElements())) {
        final AnnotationValue value = annotationMirror.getElementValues().get(member);
        final AnnotationValue defaultValue = member.getDefaultValue();
        if (value == null && defaultValue == null) {
          continue;
        }
        writeAttribute(member.getSimpleName(), value, defaultValue);
      }
      sb.append(")");
      return sb.toString();
    }

    void writeAttribute(Name simpleName, AnnotationValue value, AnnotationValue defaultValue) {
      writeAttributeKey(simpleName.toString());
      if (value != null) {
        writeVal(sb, value);
      } else {
        writeVal(sb, defaultValue);
      }
    }

    void writeAttributeKey(String name) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      sb.append("\"").append(name).append("\",");
    }
  }

  static class JakartaHandler extends StandardHandler {

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element) {
      return new JakartaHandler().writeAttributes(annotationMirror, element);
    }

    @Override
    void writeAttribute(Name simpleName, AnnotationValue value, AnnotationValue defaultValue) {
      final String name = simpleName.toString();
      if (value == null) {
        if ("message".equals(name)) {
          final String msgKey = defaultValue.toString().replace("{jakarta.validation.constraints.", "{avaje.");
          writeAttributeKey("message");
          sb.append(msgKey);
        } else if (!name.equals("payload") && !name.equals("groups")) {
          super.writeAttribute(simpleName, null, defaultValue);
        }
      } else {
        super.writeAttribute(simpleName, value, defaultValue);
      }
    }
  }

}
