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
import io.avaje.validation.adapter.ValidationContext.Message;
import io.avaje.validation.adapter.ValidationRequest;

public final class BasicAdapters {
  private BasicAdapters() {}

  public static final ValidationContext.AnnotationFactory FACTORY =
      (annotationType, context, attributes) ->
          switch (annotationType.getSimpleName()) {
            case "Email" -> new EmailAdapter(
                    context.message2("{avaje.Email.message}", attributes), attributes);
            case "Null" -> new NullAdapter(
                    context.message2("{avaje.Null.message}", attributes));
            case "NotNull" -> new NotNullAdapter(
                    context.message2("{avaje.NotNull.message}", attributes));
            case "NonNull" -> new NotNullAdapter(
                    context.message2("{avaje.NonNull.message}", attributes));
            case "AssertTrue" -> new AssertBooleanAdapter(

                    context.message2("{avaje.AssertTrue.message}", attributes), false);
            case "AssertFalse" -> new AssertBooleanAdapter(
                    context.message2("{avaje.AssertFalse.message}", attributes), true);
            case "NotBlank" -> new NotBlankAdapter(
                    context.message2("{avaje.NotBlank.message}", attributes));
            case "NotEmpty" -> new NotEmptyAdapter(
                    context.message2("{avaje.NotEmpty.message}", attributes));
            case "Past" -> new FuturePastAdapter(
                    context.message2("{avaje.Past.message}", attributes), true, false);
            case "PastOrPresent" -> new FuturePastAdapter(
                    context.message2("{avaje.PastOrPresent.message}", attributes), true, true);
            case "Future" -> new FuturePastAdapter(
                    context.message2("{avaje.Future.message}", attributes), false, false);
            case "FutureOrPresent" -> new FuturePastAdapter(
                    context.message2("{avaje.FutureOrPresent.message}", attributes), false, true);
            case "Pattern" -> new PatternAdapter(
                    context.message2("{avaje.Pattern.message}", attributes), attributes);
            case "Size" -> new SizeAdapter(
                context.message2("{avaje.Size.message}", attributes), attributes);
            default -> null;
          };

  private static final class PatternAdapter implements ValidationAdapter<CharSequence> {

    private final Message message;
    private final Predicate<String> pattern;

    @SuppressWarnings("unchecked")
    public PatternAdapter(Message message, Map<String, Object> attributes) {
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

    private final Message message;
    private final int min;
    private final int max;

    public SizeAdapter(Message message, Map<String, Object> attributes) {
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

    private final Message message;

    public NotBlankAdapter(Message message) {
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

    private final Message message;

    public NotEmptyAdapter(Message message) {
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

    private final Message message;
    private final Boolean assertBool;

    public AssertBooleanAdapter(Message message, Boolean assertBool) {
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

    private final Message message;

    public NotNullAdapter(Message message) {
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

    private final Message message;

    public NullAdapter(Message message) {
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
