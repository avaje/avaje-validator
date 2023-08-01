package io.avaje.validation.core.adapters;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.RegexFlag;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;
import io.avaje.validation.adapter.ValidationRequest;

public final class BasicAdapters {
  private BasicAdapters() {}

  public static final ValidationContext.AnnotationFactory FACTORY =
      request ->
          switch (request.annotationType().getSimpleName()) {
            case "Email" -> new EmailAdapter(request);
            case "UUID" -> new UuidAdapter(request);
            case "URI" -> new UriAdapter(request);
            case "Null" -> new NullableAdapter(request, true);
            case "NotNull", "NonNull" -> new NullableAdapter(request, false);
            case "AssertTrue" -> new AssertBooleanAdapter(request, Boolean.TRUE);
            case "AssertFalse" -> new AssertBooleanAdapter(request, Boolean.FALSE);
            case "NotBlank" -> new NotBlankAdapter(request);
            case "NotEmpty" -> new NotEmptyAdapter(request);
            case "Pattern" -> new PatternAdapter(request);
            case "Size", "Length" -> new SizeAdapter(request);
            default -> null;
          };

  static sealed class PatternAdapter extends AbstractConstraintAdapter<CharSequence>
      permits EmailAdapter {

    protected final Predicate<String> pattern;

    PatternAdapter(AdapterCreateRequest request) {
      this(request, (String) request.attribute("regexp"));
    }

    @SuppressWarnings("unchecked")
    PatternAdapter(AdapterCreateRequest request, String regex) {
      super(request);
      int flags = 0;

      final List<RegexFlag> flags1 = (List<RegexFlag>) request.attribute("flags");
      if (flags1 != null) {
        for (final var flag : flags1) {
          flags |= flag.getValue();
        }
      }
      this.pattern = Pattern.compile(regex, flags).asMatchPredicate().negate();
    }

    @Override
    public boolean isValid(CharSequence value) {
      return value == null || !pattern.test(value.toString());
    }
  }

  private static final class SizeAdapter implements ValidationAdapter<Object> {

    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;
    private final int min;
    private final int max;

    SizeAdapter(AdapterCreateRequest request) {
      this.groups = request.groups();
      this.min = (int) request.attribute("min");
      this.max = (int) request.attribute("max");
      if (min == 0 && "{avaje.Length.message}".equals(request.attribute("message"))) {
        this.message = request.message("{avaje.Length.max.message}");
      } else {
        this.message = request.message();
      }
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      if (!checkGroups(groups, req) || value == null) {
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

  private static final class NotBlankAdapter extends AbstractConstraintAdapter<CharSequence> {

    NotBlankAdapter(AdapterCreateRequest request) {
      super(request);
    }

    @Override
    public boolean isValid(CharSequence cs) {
      return (cs != null) && !isBlank(cs);
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

  private static final class NotEmptyAdapter extends AbstractConstraintAdapter<Object> {

    NotEmptyAdapter(AdapterCreateRequest request) {
      super(request);
    }

    @Override
    public boolean isValid(Object value) {
      if (value == null) {
        return false;
      } else if (value instanceof final Collection<?> col) {
        if (col.isEmpty()) {
          return false;
        }
      } else if (value instanceof final Map<?, ?> map) {
        if (map.isEmpty()) {
          return false;
        }
      } else if (value instanceof final CharSequence sequence) {
        if (sequence.length() == 0) {
          return false;
        }
      } else if (value.getClass().isArray()) {
        final var len = arrayLength(value);
        if (len == 0) {
          return false;
        }
      }
      return true;
    }
  }

  private static final class AssertBooleanAdapter extends AbstractConstraintAdapter<Boolean> {

    private final Boolean assertBool;

    AssertBooleanAdapter(AdapterCreateRequest request, Boolean assertBool) {
      super(request);
      this.assertBool = assertBool;
    }

    @Override
    public boolean isValid(Boolean type) {
      return !assertBool.booleanValue() && type == null || assertBool.equals(type);
    }
  }

  private static final class NullableAdapter extends AbstractConstraintAdapter<Object> {

    private final boolean shouldBeNull;

    NullableAdapter(AdapterCreateRequest request, boolean shouldBeNull) {
      super(request);
      this.shouldBeNull = shouldBeNull;
    }

    @Override
    public boolean isValid(Object value) {
      return (value == null) == shouldBeNull;
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
