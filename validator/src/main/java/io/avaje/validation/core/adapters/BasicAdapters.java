package io.avaje.validation.core.adapters;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.PrimitiveAdapter;
import io.avaje.validation.adapter.RegexFlag;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;
import io.avaje.validation.adapter.ValidationContext.RequestBuilder;
import io.avaje.validation.adapter.ValidationRequest;
import io.avaje.validation.spi.AnnotationFactory;

public final class BasicAdapters {
  private static final String LENGTH_MAX = "{avaje.Length.max.message}";
  private static final String NOT_NULL_MESSAGE = "{avaje.NotNull.message}";
  private static final String NULL_MESSAGE = "{avaje.Null.message}";
  private static final String NOT_BLANK_MESSAGE = "{avaje.NotBlank.message}";

  private BasicAdapters() {}

  public static AnnotationFactory factory(RequestBuilder requestBuilder) {
    return new Factory(requestBuilder);
  }

  private static final class Factory implements AnnotationFactory {

    private final NullableAdapter defaultNotNullAdapter;
    private final NullableAdapter defaultNullAdapter;
    private final NotBlankAdapter defaultNotBlankAdapter;

    Factory(RequestBuilder reqBuilder) {
      // create default adapters that will be shared instances (when no groups or message customisation)
      this.defaultNotNullAdapter = new NullableAdapter(reqBuilder.defaultRequest(NOT_NULL_MESSAGE), false);
      this.defaultNullAdapter = new NullableAdapter(reqBuilder.defaultRequest(NULL_MESSAGE), true);
      this.defaultNotBlankAdapter = new NotBlankAdapter(reqBuilder.defaultRequest(NOT_BLANK_MESSAGE));
    }

    @Override
    public ValidationAdapter<?> create(AdapterCreateRequest request) {
      return switch (request.annotationType().getSimpleName()) {
          case "Email" -> new EmailAdapter(request);
          case "UUID" -> new UuidAdapter(request);
          case "URI" -> new UriAdapter(request);
          case "Null" -> nullable(request);
          case "NotNull", "NonNull" -> notNull(request);
          case "AssertTrue" -> new AssertBooleanAdapter(request, true);
          case "AssertFalse" -> new AssertBooleanAdapter(request, false);
          case "NotBlank" -> notBlank(request);
          case "NotEmpty" -> new NotEmptyAdapter(request);
          case "Pattern" -> new PatternAdapter(request);
          case "Size", "Length" -> new SizeAdapter(request);
          case "Valid" -> new ValidAdapter(request);
          default -> null;
        };
    }

    private ValidationAdapter<?> notBlank(AdapterCreateRequest request) {
      if (NotBlankAdapter.isDefault(request)) {
        return defaultNotBlankAdapter;
      }
      return new NotBlankAdapter(request);
    }

    private ValidationAdapter<?> notNull(AdapterCreateRequest request) {
      if (request.isDefaultGroupOnly() && NOT_NULL_MESSAGE.equals(request.attribute("message"))) {
        return defaultNotNullAdapter;
      }
      return new NullableAdapter(request, false);
    }

    private ValidationAdapter<?> nullable(AdapterCreateRequest request) {
      if (request.isDefaultGroupOnly() && NULL_MESSAGE.equals(request.attribute("message"))) {
        return defaultNullAdapter;
      }
      return new NullableAdapter(request, true);
    }
  }

  static sealed class PatternAdapter extends AbstractConstraintAdapter<CharSequence>
      permits EmailAdapter {

    protected final Predicate<String> pattern;

    PatternAdapter(AdapterCreateRequest request) {
      this(request, request.attribute("regexp"));
    }

    PatternAdapter(AdapterCreateRequest request, String regex) {
      super(request);
      int flags = 0;

      final List<RegexFlag> flags1 = request.attribute("flags");
      if (flags1 != null) {
        for (final var flag : flags1) {
          flags |= flag.getValue();
        }
      }
      this.pattern = Pattern.compile(regex, flags).asMatchPredicate().negate();
    }

    @Override
    public boolean isValid(CharSequence value) {
      return !pattern.test(value.toString());
    }
  }

  private static final class SizeAdapter implements ValidationAdapter<Object> {

    private static final String LENGTH = "{avaje.Length.message}";
    private static final String SIZE = "{avaje.Size.message}";
    private static final String SIZE_MAX = "{avaje.Size.max.message}";
    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;
    private final int min;
    private final int max;

    SizeAdapter(AdapterCreateRequest request) {
      this.groups = request.groups();
      this.min = request.attribute("min");
      this.max = request.attribute("max");

      final Object msgKey = request.attribute("message");
      if (min == 0 && LENGTH.equals(msgKey)) {
        this.message = request.message(LENGTH_MAX);
      } else if (min == 0 && SIZE.equals(msgKey)) {
        this.message = request.message(useLength(request) ? LENGTH_MAX : SIZE_MAX);
      } else if (SIZE.equals(msgKey) && useLength(request)) {
        this.message = request.message(LENGTH);
      } else {
        this.message = request.message();
      }
    }

    /** Use 'Length' rather than 'Size' for string types */
    private static boolean useLength(AdapterCreateRequest request) {
      final String targetType = request.targetType();
      return "String".equals(targetType) || "CharSequence".equals(targetType);
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if (value == null || !checkGroups(groups, req)) {
        return true;
      }

      if (value instanceof final CharSequence sequence) {
        final var len = sequence.length();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
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
    private final ValidationContext.Message maxLengthMessage;
    private final Set<Class<?>> groups;
    private final int maxLength;

    NotBlankAdapter(AdapterCreateRequest request) {
      this.groups = request.groups();
      this.message = request.message();
      this.maxLength = maxLength(request);
      if (maxLength > 0 && standardMessage(request)) {
        maxLengthMessage = request.message(LENGTH_MAX, "min", 1);
      } else {
        maxLengthMessage = null;
      }
    }

    private static boolean isDefault(AdapterCreateRequest request) {
      return request.isDefaultGroupOnly()
        && standardMessage(request)
        && maxLength(request) == 0;
    }

    private static int maxLength(AdapterCreateRequest request) {
      final Integer max = request.attribute("max");
      return Objects.requireNonNullElse(max, 0);
    }

    private static boolean standardMessage(AdapterCreateRequest request) {
      return "{avaje.NotBlank.message}".equals(request.attribute("message"));
    }

    @Override
    public boolean validate(CharSequence value, ValidationRequest req, String propertyName) {
      if (!checkGroups(groups, req)) {
        return true;
      }
      if (value == null || isBlank(value)) {
        req.addViolation(message, propertyName);
        return false;
      }
      if (maxLength > 0 && value.length() > maxLength) {
        req.addViolation(maxLengthMessage != null ? maxLengthMessage : message, propertyName);
      }
      return true;
    }

    static boolean isBlank(final CharSequence cs) {
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
    private final Set<Class<?>> groups;

    NotEmptyAdapter(AdapterCreateRequest request) {
      this.groups = request.groups();
      this.message = request.message();
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if (!checkGroups(groups, req)) {
        return true;
      }
      if (invalid(value)) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }

    private boolean invalid(Object value) {
      if (value == null) {
        return true;
      } else if (value instanceof final Collection<?> col) {
        return col.isEmpty();
      } else if (value instanceof final Map<?, ?> map) {
        return map.isEmpty();
      } else if (value instanceof final CharSequence sequence) {
        return sequence.isEmpty();
      } else if (value.getClass().isArray()) {
        return arrayLength(value) == 0;
      }
      return false;
    }
  }

  private static final class AssertBooleanAdapter extends PrimitiveAdapter<Boolean> {

    private final boolean assertBool;

    AssertBooleanAdapter(AdapterCreateRequest request, boolean assertBool) {
      super(request);
      this.assertBool = assertBool;
    }

    @Override
    public boolean isValid(Boolean value) {
      return assertBool == value;
    }

    @Override
    public boolean isValid(boolean value) {
      return assertBool == value;
    }
  }

  private static final class NullableAdapter implements ValidationAdapter<Object> {

    private final boolean shouldBeNull;
    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;

    NullableAdapter(AdapterCreateRequest request, boolean shouldBeNull) {
      this.shouldBeNull = shouldBeNull;
      this.groups = request.groups();
      this.message = request.message();
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if (!checkGroups(groups, req)) {
        return true;
      }
      if ((value == null) != shouldBeNull) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }

  private static final class ValidAdapter implements ValidationAdapter<Object> {

    private final Set<Class<?>> groups;

    ValidAdapter(AdapterCreateRequest request) {
      this.groups = request.groups();
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      return checkGroups(groups, req);
    }
  }

  private static int arrayLength(Object array) {
    if (array instanceof final int[] intArr) {
      return intArr.length;
    } else if (array instanceof final boolean[] boolArr) {
      return boolArr.length;
    } else if (array instanceof final byte[] byteArr) {
      return byteArr.length;
    } else if (array instanceof final char[] charArr) {
      return charArr.length;
    } else if (array instanceof final short[] shortArr) {
      return shortArr.length;
    } else if (array instanceof final float[] floatArr) {
      return floatArr.length;
    } else if (array instanceof final double[] doubleArr) {
      return doubleArr.length;
    } else if (array instanceof final long[] longArr) {
      return longArr.length;
    } else {
      return ((Object[]) array).length;
    }
  }
}
