package io.avaje.validation.core.adapters;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
            case "Email" -> new EmailAdapter(context.message(attributes), attributes);
            case "Null" -> new NullableAdapter(context.message(attributes), true);
            case "NotNull", "NonNull" -> new NullableAdapter(context.message(attributes), false);
            case "AssertTrue" -> new AssertBooleanAdapter(context.message(attributes), Boolean.TRUE);
            case "AssertFalse" -> new AssertBooleanAdapter(context.message(attributes), Boolean.FALSE);
            case "NotBlank" -> new NotBlankAdapter(context.message(attributes));
            case "NotEmpty" -> new NotEmptyAdapter(context.message(attributes));
            case "Pattern" -> new PatternAdapter(context.message(attributes), attributes);
            case "Size" -> new SizeAdapter(context.message(attributes), attributes);
            default -> null;
          };

  private static final class PatternAdapter implements ValidationAdapter<CharSequence> {

    private final ValidationContext.Message message;
    private final Predicate<String> pattern;

    @SuppressWarnings("unchecked")
    PatternAdapter(ValidationContext.Message message, Map<String, Object> attributes) {
      this.message = message;
      int flags = 0;

      final List<RegexFlag> flags1 = (List<RegexFlag>) attributes.get("flags");
      if (flags1 != null) {
        for (final var flag : flags1) {
          flags |= flag.getValue();
        }
      }
      this.pattern = Pattern.compile((String) attributes.get("regexp"), flags).asMatchPredicate().negate();
    }

    @Override
    public boolean validate(CharSequence value, ValidationRequest req, String propertyName) {
      if (value == null) {
        return true;
      }

      if (pattern.test(value.toString())) {
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

    SizeAdapter(ValidationContext.Message message, Map<String, Object> attributes) {
      this.message = message;
      this.min = (int) attributes.get("min");
      this.max = (int) attributes.get("max");
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
      } else if (value instanceof final Collection<?> col) {
        final var len = col.size();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      } else if (value instanceof final Map<?, ?> map) {
        final var len = map.size();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      } else if (value.getClass().isArray()) {
        final var len = arrayLength(value);
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      }

      return true;
    }
  }

  private static final class NotBlankAdapter implements ValidationAdapter<CharSequence> {

    private final ValidationContext.Message message;

    NotBlankAdapter(ValidationContext.Message message) {
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

    private final ValidationContext.Message message;

    NotEmptyAdapter(ValidationContext.Message message) {
      this.message = message;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if (value == null) {
        req.addViolation(message, propertyName);
        return false;
      } else if (value instanceof final Collection<?> col) {
        if (col.isEmpty()) {
          req.addViolation(message, propertyName);
          return false;
        }
      } else if (value instanceof final Map<?, ?> map) {
        if (map.isEmpty()) {
          req.addViolation(message, propertyName);
          return false;
        }
      } else if (value instanceof final CharSequence sequence) {
        if (sequence.length() == 0) {
          req.addViolation(message, propertyName);
          return false;
        }
      } else if (value.getClass().isArray()) {
        final var len = arrayLength(value);
        if (len == 0) {
          req.addViolation(message, propertyName);
          return false;
        }
      }

      return true;
    }
  }

  private static final class AssertBooleanAdapter implements ValidationAdapter<Boolean> {

    private final ValidationContext.Message message;
    private final Boolean assertBool;

    AssertBooleanAdapter(ValidationContext.Message message, Boolean assertBool) {
      this.message = message;
      this.assertBool = assertBool;
    }

    @Override
    public boolean validate(Boolean type, ValidationRequest req, String propertyName) {
      if (!assertBool.booleanValue() && type == null) {
        return true;
      }

      if (!assertBool.equals(type)) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }

  private static final class NullableAdapter implements ValidationAdapter<Object> {

    private final ValidationContext.Message message;
    private final boolean shouldBeNull;

    NullableAdapter(ValidationContext.Message message, boolean shouldBeNull) {
      this.message = message;
      this.shouldBeNull = shouldBeNull;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if ((value == null) != shouldBeNull) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }

  private static int arrayLength(Object array) {

    if (array instanceof int[] arr) {
      return arr.length;
    } else if (array instanceof boolean[] arr) {
      return arr.length;
    } else if (array instanceof byte[] arr) {
      return arr.length;
    } else if (array instanceof char[] arr) {
      return arr.length;
    } else if (array instanceof short[] arr) {
      return arr.length;
    } else if (array instanceof float[] arr) {
      return arr.length;
    } else if (array instanceof double[] arr) {
      return arr.length;
    } else if (array instanceof long[] arr) {
      return arr.length;
    } else {
      return ((Object[]) array).length;
    }
  }
}
