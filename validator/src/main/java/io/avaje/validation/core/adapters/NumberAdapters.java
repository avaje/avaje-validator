package io.avaje.validation.core.adapters;

import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.GREATER_THAN;
import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.LESS_THAN;

import java.math.BigDecimal;
import java.util.Map;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;

public final class NumberAdapters {
  private NumberAdapters() {}

  public static final ValidationContext.AnnotationFactory FACTORY =
      (annotationType, context, attributes) ->
          switch (annotationType.getSimpleName()) {
            case "Digits" -> new DigitsAdapter(context.message("Digits", attributes), attributes);
            case "Positive" -> new PositiveAdapter(context.message("Positive", attributes));
            case "PositiveOrZero" -> new PositiveOrZeroAdapter(
                context.message("PositiveOrZero", attributes));
            case "Negative" -> new NegativeAdapter(context.message("Negative", attributes));
            case "NegativeOrZero" -> new NegativeOrZeroAdapter(
                context.message("NegativeOrZero", attributes));
            case "Max" -> new MaxAdapter(context.message("Max", attributes), attributes);
            case "Min" -> new MinAdapter(context.message("Min", attributes), attributes);

            default -> null;
          };

  private static final class MaxAdapter implements ValidationAdapter<Number> {

    private final String message;
    private final long value;

    public MaxAdapter(String message, Map<String, Object> attributes) {
      this.message = message;
      this.value = (long) attributes.get("value");
    }

    @Override
    public boolean validate(Number number, ValidationRequest req, String propertyName) {

      // null values are valid
      if (number == null) {
        return true;
      }

      if (NumberComparatorHelper.compare(number, value, GREATER_THAN) < 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class MinAdapter implements ValidationAdapter<Number> {

    private final String message;
    private final long value;

    public MinAdapter(String message, Map<String, Object> attributes) {
      this.message = message;
      this.value = (long) attributes.get("value");
    }

    @Override
    public boolean validate(Number number, ValidationRequest req, String propertyName) {

      // null values are valid
      if (number == null) {
        return true;
      }

      if (NumberComparatorHelper.compare(number, value, LESS_THAN) > 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class DigitsAdapter implements ValidationAdapter<Object> {

    private final String message;
    private final int integer;
    private final int fraction;

    public DigitsAdapter(String message, Map<String, Object> attributes) {
      this.message = message;
      this.integer = (int) attributes.get("integer");
      this.fraction = (int) attributes.get("fraction");
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      // null values are valid
      if (value == null) {
        return true;
      }

      BigDecimal bigNum;
      if (value instanceof final BigDecimal bd) {
        bigNum = bd;
      } else {
        bigNum = NumberSignHelper.getBigDecimalValue(value.toString()).stripTrailingZeros();
      }

      final int integerPartLength = bigNum.precision() - bigNum.scale();
      final int fractionPartLength = bigNum.scale() < 0 ? 0 : bigNum.scale();

      if (integer < integerPartLength || fraction < fractionPartLength) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class PositiveAdapter implements ValidationAdapter<Object> {

    private final String message;

    public PositiveAdapter(String message) {
      this.message = message;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      // null values are valid
      if (value == null) {
        return true;
      }

      if (NumberSignHelper.signum(value, LESS_THAN) <= 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class PositiveOrZeroAdapter implements ValidationAdapter<Object> {

    private final String message;

    public PositiveOrZeroAdapter(String message) {
      this.message = message;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      // null values are valid
      if (value == null) {
        return true;
      }

      if (NumberSignHelper.signum(value, LESS_THAN) < 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class NegativeAdapter implements ValidationAdapter<Object> {

    private final String message;

    public NegativeAdapter(String message) {
      this.message = message;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      // null values are valid
      if (value == null) {
        return true;
      }

      if (NumberSignHelper.signum(value, GREATER_THAN) >= 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class NegativeOrZeroAdapter implements ValidationAdapter<Object> {

    private final String message;

    public NegativeOrZeroAdapter(String message) {
      this.message = message;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      // null values are valid
      if (value == null) {
        return true;
      }

      if (NumberSignHelper.signum(value, GREATER_THAN) > 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }
}
