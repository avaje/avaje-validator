package io.avaje.validation.core;

import io.avaje.validation.adapter.RegexFlag;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;

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
import java.util.regex.Pattern;

final class JakartaAdapters {
  private JakartaAdapters() {}

  static final ValidationContext.AnnotationFactory FACTORY =
      (annotationType, context, attributes) -> {
        switch (annotationType.getSimpleName()) {
          case "NotNull":
            return new NotNullAdapter(context.message("NotNull", attributes));
          case "AssertTrue":
            return new AssertTrueAdapter(context.message("AssertTrue", attributes));
          case "NotBlank":
            return new NotBlankAdapter(context.message("NotBlank", attributes));
          case "Past":
            return new PastAdapter(context.message("Past", attributes));
          case "Pattern":
            return new PatternAdapter(context.message("Pattern", attributes), attributes);
          case "Size":
            return new SizeAdapter(context.message("Size", attributes), attributes);
          default:
            return null;
        }
      };

  private static final class PatternAdapter implements ValidationAdapter<CharSequence> {

    private final String message;
    private final Predicate<String> pattern;

    public PatternAdapter(String message, Map<String, Object> attributes) {
      this.message = message;
      int flags = 0;

      for (final var flag : (List<RegexFlag>) attributes.get("flags")) {
        flags |= flag.getValue();
      }
      this.pattern = Pattern.compile((String) attributes.get("regexp"), flags)
                      .asMatchPredicate()
                      .negate();
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

  private static final class SizeAdapter implements ValidationAdapter<Object> {

    private final String message;
    private final int min;
    private final int max;

    public SizeAdapter(String message, Map<String, Object> attributes) {
      this.message = message;
      this.min = (int) attributes.get("min");
      this.max = (int) attributes.get("max");
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

  private static final class PastAdapter implements ValidationAdapter<Object> {

    private final String message;

    public PastAdapter(String message) {
      this.message = message;
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

  private static final class NotBlankAdapter implements ValidationAdapter<String> {

    private final String message;

    public NotBlankAdapter(String message) {
      this.message = message;
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

  private static final class AssertTrueAdapter implements ValidationAdapter<Boolean> {

    private final String message;

    public AssertTrueAdapter(String message) {
      this.message = message;
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

  private static final class NotNullAdapter implements ValidationAdapter<Object> {

    private final String message;

    public NotNullAdapter(String message) {
      this.message = message;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if (value == null) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }
}
