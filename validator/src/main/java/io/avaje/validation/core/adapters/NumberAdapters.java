package io.avaje.validation.core.adapters;

import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.GREATER_THAN;
import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.LESS_THAN;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationContext.Message;
import io.avaje.validation.adapter.ValidationRequest;

public final class NumberAdapters {
  private NumberAdapters() {}

  public static final ValidationContext.AnnotationFactory FACTORY =
      (annotationType, context, attributes) ->
          switch (annotationType.getSimpleName()) {
            case "Digits" -> new DigitsAdapter(context.message2("{avaje.Digits.message}", attributes), attributes);
            case "Positive" -> new PositiveAdapter(context.message2("{avaje.Positive.message}", attributes));
            case "PositiveOrZero" -> new PositiveAdapter(
                context.message2("{avaje.PositiveOrZero.message}.message}", attributes), true);
            case "Negative" -> new NegativeAdapter(context.message2("{avaje.Negative.message}.message}", attributes));
            case "NegativeOrZero" -> new NegativeAdapter(
                context.message2("{avaje.NegativeOrZero.message}.message}", attributes), true);
            case "Max" -> new MaxAdapter(context.message2("{avaje.Max.message}", attributes), attributes);
            case "Min" -> new MinAdapter(context.message2("{avaje.Min.message}", attributes), attributes);
            case "DecimalMax" -> new DecimalMaxAdapter(
                context.message2("{avaje.DecimalMax.message}", attributes), attributes);
            case "DecimalMin" -> new DecimalMinAdapter(
                context.message2("{avaje.DecimalMin.message}", attributes), attributes);

            default -> null;
          };

  private static final class DecimalMaxAdapter implements ValidationAdapter<Number> {

    private final Message message;
    private final BigDecimal value;
    private final boolean inclusive;

    public DecimalMaxAdapter(Message message, Map<String, Object> attributes) {
      this.message = message;
      this.value = new BigDecimal((String) attributes.get("value"));
      this.inclusive = Optional.ofNullable((Boolean) attributes.get("inclusive")).orElse(true);
    }

    @Override
    public boolean validate(Number number, ValidationRequest req, String propertyName) {

      // null values are valid
      if (number == null) {
        return true;
      }

      final int comparisonResult = NumberComparatorHelper.compareDecimal(number, value, LESS_THAN);

      if (inclusive ? comparisonResult <= 0 : comparisonResult < 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class DecimalMinAdapter implements ValidationAdapter<Number> {

    private final Message message;
    private final BigDecimal value;
    private final boolean inclusive;

    public DecimalMinAdapter(Message message, Map<String, Object> attributes) {
      this.message = message;
      this.value = new BigDecimal((String) attributes.get("value"));
      this.inclusive = Optional.ofNullable((Boolean) attributes.get("inclusive")).orElse(true);
    }

    @Override
    public boolean validate(Number number, ValidationRequest req, String propertyName) {

      // null values are valid
      if (number == null) {
        return true;
      }

      final int comparisonResult = NumberComparatorHelper.compareDecimal(number, value, LESS_THAN);

      if (inclusive ? comparisonResult >= 0 : comparisonResult > 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class MaxAdapter implements ValidationAdapter<Number> {

    private final Message message;
    private final long value;

    public MaxAdapter(Message message, Map<String, Object> attributes) {
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

    private final Message message;
    private final long value;

    public MinAdapter(Message message, Map<String, Object> attributes) {
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

    private final Message message;
    private final int integer;
    private final int fraction;

    public DigitsAdapter(Message message, Map<String, Object> attributes) {
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

    private final Message message;
    private final boolean inclusive;

    public PositiveAdapter(Message message) {
      this.message = message;
      this.inclusive = false;
    }

    public PositiveAdapter(Message message, boolean inclusive) {
      this.message = message;
      this.inclusive = inclusive;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      // null values are valid
      if (value == null) {
        return true;
      }

      final int sign = NumberSignHelper.signum(value, LESS_THAN);

      if (inclusive ? sign < 0 : sign <= 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class NegativeAdapter implements ValidationAdapter<Object> {

    private final Message message;
    private final boolean inclusive;

    public NegativeAdapter(Message message, boolean inclusive) {
      this.message = message;
      this.inclusive = inclusive;
    }

    public NegativeAdapter(Message message) {
      this.message = message;
      this.inclusive = false;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      // null values are valid
      if (value == null) {
        return true;
      }

      final int sign = NumberSignHelper.signum(value, GREATER_THAN);

      if (inclusive ? sign > 0 : sign >= 0) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }
}
