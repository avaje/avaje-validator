package io.avaje.validation.core.adapters;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.avaje.validation.adapter.RegexFlag;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;

public final class BasicAdapters {
  private BasicAdapters() {}

  public static final ValidationContext.AnnotationFactory FACTORY =
      (annotationType, context, attributes) ->
          switch (annotationType.getSimpleName()) {
            case "Email" -> new EmailAdapter(context.message("Email", attributes), attributes);
            case "Null" -> new NullAdapter(context.message("Null", attributes));
            case "NotNull" -> new NotNullAdapter(context.message("NotNull", attributes));
            case "NonNull" -> new NotNullAdapter(context.message("NonNull", attributes));
            case "AssertTrue" -> new AssertBooleanAdapter(
                context.message("AssertTrue", attributes), false);
            case "AssertFalse" -> new AssertBooleanAdapter(
                context.message("AssertFalse", attributes), true);
            case "NotBlank" -> new NotBlankAdapter(context.message("NotBlank", attributes));
            case "NotEmpty" -> new NotEmptyAdapter(context.message("NotEmpty", attributes));
            case "Past" -> new FuturePastAdapter(context.message("Past", attributes), true, false);
            case "PastOrPresent" -> new FuturePastAdapter(
                context.message("PastOrPresent", attributes), true, true);
            case "Future" -> new FuturePastAdapter(
                context.message("Future", attributes), false, false);
            case "FutureOrPresent" -> new FuturePastAdapter(
                context.message("FutureOrPresent", attributes), false, true);
            case "Pattern" -> new PatternAdapter(
                context.message("Pattern", attributes), attributes);
            case "Size" -> new SizeAdapter(
                context.message2("{avaje.Size.message}", attributes), attributes);
            default -> null;
          };

  private static final class PatternAdapter implements ValidationAdapter<CharSequence> {

    private final String message;
    private final Predicate<String> pattern;

    @SuppressWarnings("unchecked")
    public PatternAdapter(String message, Map<String, Object> attributes) {
      this.message = message;
      int flags = 0;

      for (final var flag :
          Optional.ofNullable((List<RegexFlag>) attributes.get("flags")).orElseGet(List::of)) {
        flags |= flag.getValue();
      }
      this.pattern =
          Pattern.compile((String) attributes.get("regexp"), flags).asMatchPredicate().negate();
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

    private final ValidationContext.Message message;
    private final int min;
    private final int max;

    public SizeAdapter(ValidationContext.Message message, Map<String, Object> attributes) {
      this.message = message;
      this.min = Optional.ofNullable((Integer) attributes.get("min")).orElse(0);
      this.max = Optional.ofNullable((Integer) attributes.get("max")).orElse(Integer.MAX_VALUE);
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if (value == null) {
        return true;
      }

      if (value instanceof final CharSequence sequence) {
        final var len = sequence.length();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return false;
        }
      }

      if (value instanceof final Collection<?> col) {
        final var len = col.size();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      }

      if (value instanceof final Map<?, ?> map) {
        final var len = map.size();
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

  private static final class NotBlankAdapter implements ValidationAdapter<CharSequence> {

    private final String message;

    public NotBlankAdapter(String message) {
      this.message = message;
    }

    @Override
    public boolean validate(CharSequence cs, ValidationRequest req, String propertyName) {
      if (cs == null || isBlank(cs)) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }

    private static boolean isBlank(final CharSequence cs) {
      final int strLen = cs.length();
      if (strLen == 0) {
        return true;
      }
      for (int i = 0; i < strLen; i++) {
        if (!Character.isWhitespace(cs.charAt(i))) {
          return false;
        }
      }
      return true;
    }
  }

  private static final class NotEmptyAdapter implements ValidationAdapter<Object> {

    private final String message;

    public NotEmptyAdapter(String message) {
      this.message = message;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if (value == null
          || value instanceof final Collection<?> col && col.isEmpty()
          || value instanceof final Map<?, ?> map && map.isEmpty()) {
        req.addViolation(message, propertyName);
        return false;
      } else if (value instanceof final CharSequence sequence) {
        final var len = sequence.length();
        if (len == 0) {
          req.addViolation(message, propertyName);
          return false;
        }
      } else if (value.getClass().isArray()) {

        final var len = Array.getLength(value);
        if (len == 0) {
          req.addViolation(message, propertyName);
          return false;
        }
      }

      return true;
    }
  }

  // AssertFalse/AssertTrue
  private static final class AssertBooleanAdapter implements ValidationAdapter<Boolean> {

    private final String message;
    private final Boolean assertBool;

    public AssertBooleanAdapter(String message, Boolean assertBool) {
      this.message = message;
      this.assertBool = assertBool;
    }

    @Override
    public boolean validate(Boolean type, ValidationRequest req, String propertyName) {
      if (assertBool.equals(type)) {
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

  private static final class NullAdapter implements ValidationAdapter<Object> {

    private final String message;

    public NullAdapter(String message) {
      this.message = message;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if (value != null) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }
}
