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

    final var jakartaHandler = new JakartaHandler();
    handlers.put("io.avaje.validation.constraints.NotBlank", jakartaHandler);
    handlers.put("io.avaje.validation.constraints.Size", jakartaHandler);
    handlers.put("jakarta.validation.constraints.NotBlank", jakartaHandler);
    handlers.put("jakarta.validation.constraints.Size", jakartaHandler);

    final var jakartaDecimal = new JakartaDecimal();
    handlers.put("io.avaje.validation.constraints.DecimalMax", jakartaDecimal);
    handlers.put("io.avaje.validation.constraints.DecimalMin", jakartaDecimal);
    handlers.put("jakarta.validation.constraints.DecimalMax", jakartaDecimal);
    handlers.put("jakarta.validation.constraints.DecimalMin", jakartaDecimal);
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

    protected final AnnotationMirror annotationMirror;
    protected final Element element;

    /** Prototype factory */
    StandardHandler() {
      this.annotationMirror = null;
      this.element = null;
    }

    StandardHandler(AnnotationMirror annotationMirror, Element element) {
      this.annotationMirror = annotationMirror;
      this.element = element;
    }

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element) {
      return new StandardHandler(annotationMirror, element).writeAttributes();
    }

    String writeAttributes() {
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

    AnnotationValue memberValue(String nameMatch) {
      for (final ExecutableElement member : ElementFilter.methodsIn(element.getEnclosedElements())) {
        if (nameMatch.equals(member.getSimpleName().toString())) {
          return annotationMirror.getElementValues().get(member);
        }
      }
      return null;
    }
  }

  static class JakartaHandler extends StandardHandler {

    /** Prototype factory only */
    JakartaHandler() {
      super();
    }

    JakartaHandler(AnnotationMirror annotationMirror, Element element) {
      super(annotationMirror, element);
    }

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element) {
      return new JakartaHandler(annotationMirror, element).writeAttributes();
    }

    @Override
    void writeAttribute(Name simpleName, AnnotationValue value, AnnotationValue defaultValue) {
      final String name = simpleName.toString();
      if (value == null) {
        if ("message".equals(name)) {
          writeAttributeKey("message");
          sb.append(messageKey(defaultValue));
        } else if (!name.equals("payload") && !name.equals("groups")) {
          super.writeAttribute(simpleName, null, defaultValue);
        }
      } else {
        super.writeAttribute(simpleName, value, defaultValue);
      }
    }

    String messageKey(AnnotationValue defaultValue) {
      return defaultValue.toString().replace("{jakarta.validation.constraints.", "{avaje.");
    }
  }

  static class JakartaDecimal extends JakartaHandler {

    /** Prototype factory only */
    JakartaDecimal() {
      super();
    }

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element) {
      return new JakartaDecimal(annotationMirror, element).writeAttributes();
    }

    JakartaDecimal(AnnotationMirror annotationMirror, Element element) {
      super(annotationMirror, element);
    }

    String messageKey(AnnotationValue defaultValue) {
      final AnnotationValue inclusiveValue = memberValue("inclusive");
      final boolean inclusive = (inclusiveValue == null || "true".equals(inclusiveValue.toString()));
      String messageKey = super.messageKey(defaultValue);
      if (!inclusive) {
        messageKey = messageKey.replace(".message", ".exclusive.message");
      }
      return messageKey;
    }
  }

}
