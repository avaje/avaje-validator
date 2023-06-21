package io.avaje.validation.generator;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

final class AnnotationUtil {

  interface Handler {
    String attributes(AnnotationMirror annotationMirror, Element element);

    String attributes(Map<String, Object> attributeMap, TypeElement element);

  }

  static final Handler defaultHandler = new StandardHandler();
  static final Map<String, Handler> handlers = new HashMap<>();
  static {
    final var pattern = new PatternHandler();
    handlers.put("io.avaje.constraints.Pattern", pattern);
    handlers.put("jakarta.validation.constraints.Pattern", pattern);

    final var decimalHandler = new DecimalHandler();
    handlers.put("io.avaje.validation.constraints.DecimalMax", decimalHandler);
    handlers.put("io.avaje.validation.constraints.DecimalMin", decimalHandler);
    handlers.put("jakarta.validation.constraints.DecimalMax", decimalHandler);
    handlers.put("jakarta.validation.constraints.DecimalMin", decimalHandler);

    final var commonHandler = new CommonHandler();
    final String[] keys = {"AssertFalse", "AssertTrue", "Null", "NotNull", "NotBlank", "NotEmpty",
      "Size", "Email", "Past", "PastOrPresent", "Future", "FutureOrPresent",
      "Digits", "Positive", "PositiveOrZero", "Negative", "NegativeOrZero", "Max", "Min"};
    for (final String key : keys) {
      handlers.put("io.avaje.validation.constraints." + key, commonHandler);
      handlers.put("jakarta.validation.constraints." + key, commonHandler);
    }
  }

  private AnnotationUtil() {}

  static String annotationAttributeMap(AnnotationMirror annotationMirror) {
    final Element element = annotationMirror.getAnnotationType().asElement();
    final Handler handler = handlers.get(element.toString());
    return Objects.requireNonNullElse(handler, defaultHandler).attributes(annotationMirror, element);
  }

  static String annotationAttributeMap(String annotationStr) {
	  final  String result;
    final var start = annotationStr.indexOf('(');
  final  String attributes;
    if (start == -1) {result = annotationStr;attributes="";}
    else {result = annotationStr.substring(0, start);
    attributes= annotationStr.substring(start,annotationStr.lastIndexOf(')'));}
   final var element= ProcessingContext.element(result);
    final Map<String, Object> attributeMap= Arrays.stream(splitString(attributes))
    		.map(s -> s.split("="))
    		.collect(toMap(a->a[0], a->a[1]));

   ElementFilter.methodsIn(element.getEnclosedElements())
   .forEach(e->{
	   attributeMap.compute(e.getSimpleName().toString(), (k,v)->{

		   if(v== null) return e.getDefaultValue().getValue();

		   if(v instanceof String s) {
			  var returnType= e.getReturnType();
			  if( returnType instanceof ArrayType at) {
				 Util.stripBrackets(s).;
			  }
		   }
		   return null;

	   });

   });


    final Handler handler = handlers.get(result);

    return Objects.requireNonNullElse(handler, defaultHandler)
        .attributes(attributeMap, ProcessingContext.element(result));
  }

  public static String[] splitString(String input) {
      final Pattern pattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
      return pattern.split(input);
  }

  abstract static class BaseHandler implements Handler {
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
    @SuppressWarnings("unchecked")
    final void writeVal(final StringBuilder sb, final String value) {
      // handle array values
      if (value.) {
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
      } else {
        sb.append(value);
      }
    }
  }



  /** Convert default Jakarta message keys to avaje keys */
  private static String avajeKey(String messageKey) {
    return messageKey.replace("{jakarta.validation.constraints.", "{avaje.");
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

      sb.append("\"regexp\",\"").append(prism.regexp()).append("\"");

      if (prism.message() != null) {
        sb.append(", \"message\",\"").append(avajeKey(prism.message())).append("\"");
      }
      if (!prism.flags().isEmpty()) {
          sb.append(", \"flags\",List.of(").append(String.join(", ", prism.flags())).append(")");
        }
      if (!prism.groups().isEmpty()) {
          sb.append(", \"groups\",List.of(").append(String.join(", ", prism.groups()+".class")).append(")");
        }
      sb.append(")");
    }

    @Override
    public String attributes(Map<String, Object> result, TypeElement e) {

      sb.append("\"regexp\",\"").append(result.get("regexp")).append("\"");
      final var message = result.get("message");
      if (message != null) {
        sb.append(", \"message\",\"").append(avajeKey((String) message)).append("\"");
      }

      var flags =(String) result.get("flags");

      if (flags != null) {
        flags = Util.stripBrackets( flags);
        flags = Arrays.stream(flags.split(",")).map(Util::shortName).collect(joining(", "));

        sb.append(", \"flags\",List.of(").append(flags).append(")");
      }

      String groups = (String) result.get("groups");
      if (groups != null) {

        groups = Util.stripBrackets(groups);

        sb.append(", \"groups\",List.of(").append(groups).append(")");
      }

      sb.append(")");
      return sb.toString();
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

    @Override
    public String attributes(Map<String, String> attributeMap, TypeElement element) {

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
  }

  static class CommonHandler extends StandardHandler {

    /** Prototype factory only */
    CommonHandler() {
    }

    CommonHandler(AnnotationMirror annotationMirror, Element element) {
      super(annotationMirror, element);
    }

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element) {
      return new CommonHandler(annotationMirror, element).writeAttributes();
    }

    @Override
    void writeAttribute(Name simpleName, AnnotationValue value, AnnotationValue defaultValue) {
      final String name = simpleName.toString();
      if (value == null) {
        if ("message".equals(name)) {
          writeAttributeKey("message");
          sb.append(messageKey(defaultValue));
        } else if (!"payload".equals(name) && !"groups".equals(name)) {
          super.writeAttribute(simpleName, null, defaultValue);
        }
      } else {
        super.writeAttribute(simpleName, value, defaultValue);
      }
    }

    String messageKey(AnnotationValue defaultValue) {
      return avajeKey(defaultValue.toString());
    }
  }

  static class DecimalHandler extends CommonHandler {

    /** Prototype factory only */
    DecimalHandler() {
    }

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element) {
      return new DecimalHandler(annotationMirror, element).writeAttributes();
    }

    DecimalHandler(AnnotationMirror annotationMirror, Element element) {
      super(annotationMirror, element);
    }

    @Override
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
