package io.avaje.validation.core;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.avaje.validation.adapter.AnnotationValidationAdapter;
import io.avaje.validation.adapter.AnnotationValidatorFactory;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.Pattern.Flag;

final class JakartaTypeAdapters {
  private JakartaTypeAdapters() {}

  static final AnnotationValidatorFactory FACTORY =
      (annotationType, context, interpolator) -> {
        switch (annotationType.getSimpleName()) {
          case "AssertTrue":
            return new AssertTrueAdapter(interpolator);
          case "NotBlank":
            return new NotBlankAdapter(interpolator);
          case "Past":
            return new PastAdapter(interpolator);
          case "Pattern":
            return new PatternAdapter(interpolator);
          case "Size":
            return new SizeAdapter(interpolator);
          default:
            return null;
        }
      };

  private static final class PatternAdapter implements AnnotationValidationAdapter<CharSequence> {

    private String message;
    private final MessageInterpolator interpolator;
    private Predicate<String> pattern;

    public PatternAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<CharSequence> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));

      int flags = 0;

      for (final var flag : (List<Flag>) annotationValueMap.get("flags")) {
        flags |= flag.getValue();
      }

      pattern =
          java.util.regex.Pattern.compile((String) annotationValueMap.get("regexp"), flags)
              .asMatchPredicate()
              .negate();

      return this;
    }

    @Override
    public boolean validate(CharSequence value, ValidationRequest req, String propertyName) {

      if (value == null || pattern.test(propertyName)) {

        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class SizeAdapter implements AnnotationValidationAdapter<Object> {

    private String message;
    private final MessageInterpolator interpolator;
    private int min;
    private int max;

    public SizeAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<Object> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));
      min = (int) annotationValueMap.get("min");
      max = (int) annotationValueMap.get("max");
      return this;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      if (value == null) {
        if (min != -1) {
          req.addViolation("CollectionNull", propertyName);
        }
        return false;
      }

      if (value instanceof CharSequence) {
        final var sequence = (CharSequence) value;
        final var len = sequence.length();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return false;
        }
      }

      if (value instanceof Collection<?>) {
        final var col = (Collection<?>) value;
        final var len = col.size();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      }

      if (value instanceof Map<?, ?>) {
        final var col = (Map<?, ?>) value;
        final var len = col.size();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      }

      if (value.getClass().isArray()) {

        final var len = Array.getLength(value);
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      }

      return true;
    }
  }

  private static final class PastAdapter implements AnnotationValidationAdapter<Object> {

    private String message;
    private final MessageInterpolator interpolator;

    public PastAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<Object> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));
      return this;
    }

    @Override
    public boolean validate(Object obj, ValidationRequest req, String propertyName) {

      if (obj == null) {
        req.addViolation(message, propertyName);
        return false;
      } else if (obj instanceof Date) {
        final Date date = (Date) obj;
        if (date.after(Date.from(Instant.now()))) {
          req.addViolation(message, propertyName);
          return false;
        }
      } else if (obj instanceof TemporalAccessor) {

        final TemporalAccessor temporalAccessor = (TemporalAccessor) obj;
        if (temporalAccessor instanceof LocalDate) {
          if (LocalDate.from(temporalAccessor).isAfter(LocalDate.now())) {
            req.addViolation(message, propertyName);
            return false;
          }
        } else if (temporalAccessor instanceof LocalTime) {
          final LocalTime localTime = (LocalTime) temporalAccessor;
          // handle LocalTime

          // TODO do the rest of them
        }
      }
      return true;
    }
  }

  private static final class NotBlankAdapter implements AnnotationValidationAdapter<String> {

    private String message;
    private final MessageInterpolator interpolator;

    public NotBlankAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<String> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));
      return this;
    }

    @Override
    public boolean validate(String str, ValidationRequest req, String propertyName) {
      if (str == null || str.isBlank()) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }

  private static final class AssertTrueAdapter implements AnnotationValidationAdapter<Boolean> {

    private String message;
    private final MessageInterpolator interpolator;

    public AssertTrueAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<Boolean> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));
      return this;
    }

    @Override
    public boolean validate(Boolean type, ValidationRequest req, String propertyName) {
      if (Boolean.FALSE.equals(type)) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }
}
