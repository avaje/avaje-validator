package io.avaje.validation.generator;

import static io.avaje.validation.generator.ProcessingContext.isAssignable2Interface;
import static io.avaje.validation.generator.Util.trimAnnotations;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.util.*;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

final class AnnotationUtil {

  interface Handler {
    String attributes(AnnotationMirror annotationMirror, Element element, Element target);

    String attributes(Map<String, Object> attributeMap);
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
    final String[] keys = {
      "AssertFalse",
      "AssertTrue",
      "Null",
      "NotNull",
      "NotBlank",
      "NotEmpty",
      "Email",
      "Length",
      "Range",
      "Max",
      "Min",
      "Size",
      "Past",
      "PastOrPresent",
      "Future",
      "FutureOrPresent",
      "Digits",
      "Positive",
      "PositiveOrZero",
      "Negative",
      "NegativeOrZero",
    };
    for (final String key : keys) {
      handlers.put("io.avaje.validation.constraints." + key, commonHandler);
      handlers.put("jakarta.validation.constraints." + key, commonHandler);
    }
  }

  static Map<String,String> KNOWN_TYPES = new HashMap<>();
  static {
    KNOWN_TYPES.put("byte", "Byte");
    KNOWN_TYPES.put("java.lang.Byte", "Byte");
    KNOWN_TYPES.put("short", "Short");
    KNOWN_TYPES.put("java.lang.Short", "Short");
    KNOWN_TYPES.put("int", "Integer");
    KNOWN_TYPES.put("java.lang.Integer", "Integer");
    KNOWN_TYPES.put("java.util.OptionalInt", "Integer");
    KNOWN_TYPES.put("long", "Long");
    KNOWN_TYPES.put("java.lang.Long", "Long");
    KNOWN_TYPES.put("java.util.OptionalLong", "Long");
    KNOWN_TYPES.put("float", "Float");
    KNOWN_TYPES.put("java.lang.Float", "Float");
    KNOWN_TYPES.put("double", "Double");
    KNOWN_TYPES.put("java.lang.Double", "Double");
    KNOWN_TYPES.put("java.util.OptionalDouble", "Double");
    KNOWN_TYPES.put("java.math.BigDecimal", "BigDecimal");
    KNOWN_TYPES.put("java.math.BigInteger", "BigInteger");
    KNOWN_TYPES.put("java.lang.String", "String");
    //TODO; Consider java.time types
  }

  static String lookupType(TypeMirror typeMirror) {
    String rawType = trimAnnotations(typeMirror.toString());
    final String val = KNOWN_TYPES.get(rawType);
    if (val != null) {
      return val;
    }
    if (isAssignable2Interface(rawType, "java.math.BigDecimal")) {
      return "BigDecimal";
    }
    if (isAssignable2Interface(rawType, "java.math.BigInteger")) {
      return "BigInteger";
    }
    if (isAssignable2Interface(rawType, "java.lang.Number")) {
      return "Number";
    }
    if (isAssignable2Interface(rawType, "java.lang.CharSequence")) {
      return "CharSequence";
    }
    return null;
  }

  private AnnotationUtil() {}

  static String annotationAttributeMap(AnnotationMirror annotationMirror, Element target) {
    final Element element = annotationMirror.getAnnotationType().asElement();
    final Handler handler = handlers.get(element.toString());
    return Objects.requireNonNullElse(handler, defaultHandler)
        .attributes(annotationMirror, element, target);
  }

  static String annotationAttributeMap(String annotationStr) {
    String result;
    final var start = annotationStr.indexOf('(');
    final String attributes;
    if (start == -1) {
      result = annotationStr;
      attributes = "";
    } else {
      result = annotationStr.substring(0, start);
      attributes = annotationStr.substring(start + 1, annotationStr.lastIndexOf(')'));
    }
    final var element = ProcessingContext.element(result);
    final Map<String, Object> attributeMap =
        Arrays.stream(splitString(attributes, ","))
            .map(s -> splitString(s, "="))
            .filter(a -> a.length == 2)
            .collect(toMap(a -> a[0], a -> a[1]));
    convertTypeUse(element, attributeMap);

    final Handler handler = handlers.get(result);
    return Objects.requireNonNullElse(handler, defaultHandler).attributes(attributeMap);
  }

  private static void convertTypeUse(
      final TypeElement element, final Map<String, Object> attributeMap) {
    // convert attribute map values into proper types
    // and add default values if needed
    for (final var e : ElementFilter.methodsIn(element.getEnclosedElements())) {

      final var returnType = e.getReturnType();
      attributeMap.compute(
          e.getSimpleName().toString(),
          (k, v) -> {
            if (v == null) {
              final var defaultVal = e.getDefaultValue().getValue();

              if (defaultVal instanceof final List l) {

                v = l.isEmpty() ? "{ }" : l;
              } else {
                return switchType(returnType.toString(), e.getDefaultValue().getValue().toString());
              }
            }
            if (v instanceof final String s) {
              if (returnType instanceof final ArrayType at) {
                final var type = at.getComponentType().toString();

                v =
                    Arrays.stream(splitString(Util.stripBrackets(s), ","))
                        .filter(not(String::isBlank))
                        .map(ae -> switchType(type, ae))
                        .toList();

              } else {

                v = switchType(returnType.toString(), s);
              }
            }
            return v;
          });
    }
  }

  private static Object switchType(final String type, String s) {

    return switch (type) {
      case "long" -> Long.parseLong(s);
      case "double" -> Double.parseDouble(s);
      case "float" -> Float.parseFloat(s);
      case "int" -> Integer.parseInt(s);
      case "java.lang.String" -> s.startsWith("\"") && s.endsWith("\"") ? s : "\"" + s + "\"";
      default -> s;
    };
  }

  public static String[] splitString(String input, String delimiter) {
    final Pattern pattern = Pattern.compile(delimiter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
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

    final void writeVal(final StringBuilder sb, final Object value) {
      // handle array values
      if (value instanceof final List l) {
        sb.append("List.of(");
        boolean first = true;

        for (final Object listValue : l) {
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
    public String attributes(AnnotationMirror annotationMirror, Element element, Element target) {
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
        sb.append(", \"groups\",List.of(")
            .append(String.join(", ", prism.groups() + ".class"))
            .append(")");
      }
      sb.append(")");
    }

    @Override
    public String attributes(Map<String, Object> attributes) {

      sb.append("\"regexp\",\"").append(attributes.get("regexp")).append("\"");
      final var message = attributes.get("message");
      if (message != null) {
        sb.append(", \"message\",\"").append(avajeKey((String) message)).append("\"");
      }

      var flags = (String) attributes.get("flags");

      if (flags != null) {
        flags = Util.stripBrackets(flags);
        flags = Arrays.stream(flags.split(",")).map(Util::shortName).collect(joining(", "));

        sb.append(", \"flags\",List.of(").append(flags).append(")");
      }

      String groups = (String) attributes.get("groups");
      if (groups != null) {

        groups = Util.stripBrackets(groups);

        sb.append(", \"groups\",Set.of(").append(groups).append(")");
      }

      sb.append(")");
      // for some reason it wasn't resetting, so manual reset of sb
      final var result = sb.toString();
      sb.setLength(0);
      sb.append("Map.of(");
      first = true;
      return result;
    }
  }

  static class StandardHandler extends BaseHandler {

    protected final AnnotationMirror annotationMirror;
    protected final Element element;
    protected final Element target;

    /** Prototype factory */
    StandardHandler() {
      this.annotationMirror = null;
      this.element = null;
      this.target = null;
    }

    StandardHandler(AnnotationMirror annotationMirror, Element element, Element target) {
      this.annotationMirror = annotationMirror;
      this.element = element;
      this.target = target;
    }

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element, Element target) {
      return new StandardHandler(annotationMirror, element, target).writeAttributes();
    }

    String writeAttributes() {
      for (final ExecutableElement member :
          ElementFilter.methodsIn(element.getEnclosedElements())) {
        final AnnotationValue value = annotationMirror.getElementValues().get(member);
        final AnnotationValue defaultValue = member.getDefaultValue();
        if (value == null && defaultValue == null) {
          continue;
        }
        writeAttribute(member.getSimpleName(), value, defaultValue);
      }
      writeTypeAttribute(target.asType());
      sb.append(")");
      return sb.toString();
    }

    protected void writeTypeAttribute(TypeMirror typeMirror) {
      String _type = lookupType(typeMirror);
      if (_type != null) {
        writeAttributeKey("_type");
        sb.append('"').append(_type).append('"');
      }
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
      for (final ExecutableElement member :
          ElementFilter.methodsIn(element.getEnclosedElements())) {
        if (nameMatch.equals(member.getSimpleName().toString())) {
          return annotationMirror.getElementValues().get(member);
        }
      }
      return null;
    }

    @Override
    public String attributes(Map<String, Object> attributeMap) {

      for (final var entry : attributeMap.entrySet()) {
        writeAttributeKey(entry.getKey());
        writeVal(sb, entry.getValue());
      }
      sb.append(")");

      // for some reason it wasn't resetting, so manual reset of sb
      final var result = sb.toString();
      sb.setLength(0);
      sb.append("Map.of(");
      first = true;
      return result;
    }
  }

  static class CommonHandler extends StandardHandler {

    /** Prototype factory only */
    CommonHandler() {}

    CommonHandler(AnnotationMirror annotationMirror, Element element, Element target) {
      super(annotationMirror, element, target);
    }

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element, Element target) {
      return new CommonHandler(annotationMirror, element, target).writeAttributes();
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
    DecimalHandler() {}

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element, Element target) {
      return new DecimalHandler(annotationMirror, element, target).writeAttributes();
    }

    DecimalHandler(AnnotationMirror annotationMirror, Element element, Element target) {
      super(annotationMirror, element, target);
    }

    @Override
    String messageKey(AnnotationValue defaultValue) {
      final AnnotationValue inclusiveValue = memberValue("inclusive");
      final boolean inclusive =
          (inclusiveValue == null || "true".equals(inclusiveValue.toString()));
      String messageKey = super.messageKey(defaultValue);
      if (!inclusive) {
        messageKey = messageKey.replace(".message", ".exclusive.message");
      }
      return messageKey;
    }
  }
}
