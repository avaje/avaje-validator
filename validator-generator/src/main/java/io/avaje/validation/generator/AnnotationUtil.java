package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.isAssignable;
import static io.avaje.validation.generator.APContext.logError;
import static io.avaje.validation.generator.ElementAnnotationContainer.hasMetaConstraintAnnotation;
import static io.avaje.validation.generator.ProcessorUtils.trimAnnotations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

final class AnnotationUtil {

  interface Handler {
    String attributes(AnnotationMirror annotationMirror, Element element, Element target);

  }

  private static final Map<String,String> KNOWN_TYPES = new HashMap<>();
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
    KNOWN_TYPES.put("java.lang.CharSequence", "String");
    KNOWN_TYPES.put("boolean", "Boolean");
    KNOWN_TYPES.put("java.lang.Boolean", "Boolean");
    //TODO; Consider java.time types
  }

  static Set<String> NUMBER_TYPES = new HashSet<>();
  static {
    NUMBER_TYPES.add("Byte");
    NUMBER_TYPES.add("Short");
    NUMBER_TYPES.add("Integer");
    NUMBER_TYPES.add("Long");
    NUMBER_TYPES.add("Float");
    NUMBER_TYPES.add("Double");
    NUMBER_TYPES.add("BigDecimal");
    NUMBER_TYPES.add("BigInteger");
    NUMBER_TYPES.add("Number");
  }

  /** These annotations should only be used on numeric types */
  private static final String[] NUMBER_TYPE_ONLY_ANNOTATIONS = {"Max", "Min", "Positive", "PositiveOrZero", "Negative", "NegativeOrZero"};
  private static final String[] BOOLEAN_TYPE_ONLY_ANNOTATIONS = {"AssertTrue", "AssertFalse"};
  private static final String[] STRING_TYPE_ONLY_ANNOTATIONS = {"NotBlank","Email"};
  private static final String[] TEMPORAL_ONLY_ANNOTATIONS = {"Past", "PastOrPresent", "Future", "FutureOrPresent", "DateRange"};

  private static final Handler defaultHandler = new StandardHandler();

  private static final Map<String, Handler> handlers = new HashMap<>();
  static {
    final var pattern = new PatternHandler();
    register(pattern, "Pattern");

    final var decimalHandler = new DecimalHandler();
    for (String key : List.of("DecimalMax", "DecimalMin")) {
      register(decimalHandler, key);
    }

    final var numberHandler = new TypeCheckingHandler(new HandlerMeta(NUMBER_TYPES, "non-numeric", false));
    for (final String key : NUMBER_TYPE_ONLY_ANNOTATIONS) {
      register(numberHandler, key);
    }

    final var booleanHandler = new TypeCheckingHandler(new HandlerMeta(Set.of("Boolean"), "non-boolean", true));
    for (final String key : BOOLEAN_TYPE_ONLY_ANNOTATIONS) {
      register(booleanHandler, key);
    }

    final var temporalHandler = new TypeCheckingHandler(new TemporalMeta());
    for (final String key : TEMPORAL_ONLY_ANNOTATIONS) {
      register(temporalHandler, key);
    }

    final var stringOnlyHandler = new TypeCheckingHandler(new HandlerMeta(Set.of("String", "CharSequence"), "non-string", true));
    for (final String key : STRING_TYPE_ONLY_ANNOTATIONS) {
      register(stringOnlyHandler, key);
    }

    final var commonHandler = new CommonHandler();
    final String[] keys = {
      "Null",
      "NotNull",
      "NotEmpty",
      "Length",
      "Range",
      "Size",
      "Digits",
      "URI",
      "UUID",
    };
    for (final String key : keys) {
      register(commonHandler, key);
    }
  }

  private static void register(Handler handler, String key) {
    handlers.put("io.avaje.validation.constraints." + key, handler);
    handlers.put("jakarta.validation.constraints." + key, handler);
  }

  static String lookupType(TypeMirror typeMirror) {
    String rawType = trimAnnotations(typeMirror.toString());
    final String val = KNOWN_TYPES.get(rawType);
    if (val != null) {
      return val;
    }
    if (isAssignable(rawType, "java.math.BigDecimal")) {
      return "BigDecimal";
    }
    if (isAssignable(rawType, "java.math.BigInteger")) {
      return "BigInteger";
    }
    if (isAssignable(rawType, "java.lang.Number")) {
      return "Number";
    }
    if (isAssignable(rawType, "java.lang.CharSequence")) {
      return "CharSequence";
    }
    if (isAssignable(rawType, "java.time.temporal.Temporal")) {
      return "Temporal." + Util.shortName(rawType);
    }
    if (isAssignable(rawType, "java.util.Date")) {
      return "Temporal.Date";
    }
    if (rawType.contains("[]")) {
      return "Array";
    }
    return null;
  }

  private AnnotationUtil() {}

  static String annotationAttributeMap(AnnotationMirror annotationMirror, Element target) {
    final var element = APContext.asTypeElement(annotationMirror.getAnnotationType());
    final Handler handler = handlers.get(element.getQualifiedName().toString());
    return Objects.requireNonNullElse(handler, defaultHandler)
        .attributes(annotationMirror, element, target);
  }

  static String[] splitString(String input, String delimiter) {
    final Pattern pattern = Pattern.compile(delimiter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    return pattern.split(input);
  }

  abstract static class BaseHandler implements Handler {
    final StringBuilder sb = new StringBuilder("Map.of(");
    boolean first = true;

    void validate() {
      // do nothing by default
    }

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
      sb.append("\"regexp\",\"").append(escape(prism.regexp())).append("\"");
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

    /**
     * Escapes special characters for a Java String literal.
     */
    private static String escape(String value) {
      if (value == null) return "";
      return value.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
  }

  static class StandardHandler extends BaseHandler {

    protected final AnnotationMirror annotationMirror;
    protected final Element element;
    protected final Element target;
    protected final String _type;

    /** Prototype factory */
    StandardHandler() {
      this.annotationMirror = null;
      this.element = null;
      this.target = null;
      this._type = null;
    }

    StandardHandler(AnnotationMirror annotationMirror, Element element, Element target) {
      this.annotationMirror = annotationMirror;
      this.element = element;
      this.target = target;
      this._type = lookupType(target.asType());
    }

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element, Element target) {
      return new StandardHandler(annotationMirror, element, target).writeAttributes();
    }

    String writeAttributes() {
      validate();
      for (final ExecutableElement member : ElementFilter.methodsIn(element.getEnclosedElements())) {
        final AnnotationValue value = annotationMirror.getElementValues().get(member);
        final AnnotationValue defaultValue = member.getDefaultValue();
        if (value == null && defaultValue == null) {
          continue;
        }
        writeAttribute(member.getSimpleName(), value, defaultValue);
      }
      writeTypeAttribute();
      sb.append(")");
      return sb.toString();
    }

    protected void writeTypeAttribute() {
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
      for (final ExecutableElement member : ElementFilter.methodsIn(element.getEnclosedElements())) {
        if (nameMatch.equals(member.getSimpleName().toString())) {
          return annotationMirror.getElementValues().get(member);
        }
      }
      return null;
    }
  }

  interface SupportedMeta {

    String message(AnnotationMirror annotationMirror, Element target);

    boolean isSupported(Element target, String _type);
  }


  static final class TemporalMeta implements SupportedMeta {

    @Override
    public String message(AnnotationMirror annotationMirror, Element target) {
      return "Not allowed to use " + annotationMirror + " on a non-temporal type for " + target;
    }

    @Override
    public boolean isSupported(Element target, String _type) {
      boolean isMetaConstraint = hasMetaConstraintAnnotation(target);
      return isMetaConstraint || _type == null || _type.startsWith("Temporal.");
    }
  }

  static final class HandlerMeta implements SupportedMeta {

    private final Set<String> supportedTypes;
    private final String message;
    private final boolean allowUnknown;

    HandlerMeta(Set<String> supportedTypes, String message, boolean allowUnknown) {
      this.supportedTypes = supportedTypes;
      this.message = message;
      this.allowUnknown = allowUnknown;
    }

    @Override
    public String message(AnnotationMirror annotationMirror, Element target) {
      return "Not allowed to use " + annotationMirror + " on a " + message + " type for " + target;
    }

    @Override
    public boolean isSupported(Element target, String _type) {
      boolean isMetaConstraint = hasMetaConstraintAnnotation(target);
      return isMetaConstraint || allowUnknown && _type == null || _type != null && supportedTypes.contains(_type);
    }
  }

  /** Adds validation that the type this constraint is applied to is a numeric type */
  static class TypeCheckingHandler extends CommonHandler {
    private final SupportedMeta meta;

    TypeCheckingHandler(SupportedMeta meta) {
      this.meta = meta;
    }

    TypeCheckingHandler(SupportedMeta meta, AnnotationMirror annotationMirror, Element element, Element target) {
      super(annotationMirror, element, target);
      this.meta = meta;
    }

    @Override
    public String attributes(AnnotationMirror annotationMirror, Element element, Element target) {
      return new TypeCheckingHandler(meta, annotationMirror, element, target).writeAttributes();
    }

    @Override
    void validate() {
      if (!meta.isSupported(target, _type)) {
        logError(target, meta.message(annotationMirror, target));
      }
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

  /** Adds inclusive/exclusive message key handling */
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
      final boolean inclusive = inclusiveValue == null || "true".equals(inclusiveValue.toString());
      String messageKey = super.messageKey(defaultValue);
      if (!inclusive) {
        messageKey = messageKey.replace(".message", ".exclusive.message");
      }
      return messageKey;
    }
  }
}
