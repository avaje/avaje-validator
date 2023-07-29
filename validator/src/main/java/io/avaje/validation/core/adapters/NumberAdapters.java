package io.avaje.validation.core.adapters;

import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.GREATER_THAN;
import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.LESS_THAN;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;

public final class NumberAdapters {
  private NumberAdapters() {}

  public static final ValidationContext.AnnotationFactory FACTORY =
      (annotationType, context, groups, attributes) ->
          switch (annotationType.getSimpleName()) {
            case "Digits" -> new DigitsAdapter(context.message(attributes), groups, attributes);
            case "Positive" -> new PositiveAdapter(context.message(attributes), groups, false);
            case "PositiveOrZero" -> new PositiveAdapter(context.message(attributes), groups, true);
            case "Negative" -> new NegativeAdapter(context.message(attributes), groups, false);
            case "NegativeOrZero" -> new NegativeAdapter(context.message(attributes), groups, true);
            case "Max" -> new MaxAdapter(context.message(attributes), groups, attributes);
            case "Min" -> new MinAdapter(context.message(attributes), groups, attributes);
            case "DecimalMax" -> new DecimalMaxAdapter(
                context.message(attributes), groups, attributes);
            case "DecimalMin" -> new DecimalMinAdapter(
                context.message(attributes), groups, attributes);
            case "Range" -> new RangeAdapter(context.message(attributes), groups, attributes);
            default -> null;
          };

  private static final class DecimalMaxAdapter extends AbstractConstraintAdapter<Number> {

    private final BigDecimal value;
    private final boolean inclusive;

    DecimalMaxAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {
      super(message, groups);
      this.value = new BigDecimal((String) attributes.get("value"));
      this.inclusive = Optional.ofNullable((Boolean) attributes.get("inclusive")).orElse(true);
    }

    @Override
    public boolean isValid(Number number) {
      // null values are valid
      if (number == null) {
        return true;
      }

      final int comparisonResult = NumberComparatorHelper.compareDecimal(number, value, LESS_THAN);

      return !(inclusive ? comparisonResult > 0 : comparisonResult >= 0);
    }
  }

  private static final class DecimalMinAdapter extends AbstractConstraintAdapter<Number> {

    private final BigDecimal value;
    private final boolean inclusive;

    DecimalMinAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {

      super(message, groups);
      this.value = new BigDecimal((String) attributes.get("value"));
      this.inclusive = Optional.ofNullable((Boolean) attributes.get("inclusive")).orElse(true);
    }

    @Override
    public boolean isValid(Number number) {
      // null values are valid
      if (number == null) {
        return true;
      }

      final int comparisonResult = NumberComparatorHelper.compareDecimal(number, value, LESS_THAN);

      return !(inclusive ? comparisonResult < 0 : comparisonResult <= 0);
    }
  }

  private static final class MaxAdapter extends AbstractConstraintAdapter<Number> {

    private final long value;

    MaxAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {

      super(message, groups);
      this.value = (long) attributes.get("value");
    }

    MaxAdapter(ValidationContext.Message message, Set<Class<?>> groups, long value) {

      super(message, groups);
      this.value = value;
    }

    @Override
    public boolean isValid(Number number) {
      // null values are valid

      return number == null || NumberComparatorHelper.compare(number, value, GREATER_THAN) <= 0;
    }
  }

  private static final class MinAdapter extends AbstractConstraintAdapter<Number> {

    private final long value;

    MinAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {

      super(message, groups);
      this.value = (long) attributes.get("value");
    }

    MinAdapter(ValidationContext.Message message, Set<Class<?>> groups, long value) {

      super(message, groups);
      this.value = value;
    }

    @Override
    public boolean isValid(Number number) {
      // null values are valid

      return number == null || NumberComparatorHelper.compare(number, value, LESS_THAN) >= 0;
    }
  }

  private static final class DigitsAdapter extends AbstractConstraintAdapter<Object> {

    private final int integer;
    private final int fraction;

    DigitsAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {

      super(message, groups);
      this.integer = (int) attributes.get("integer");
      this.fraction = (int) attributes.get("fraction");
    }

    @Override
    public boolean isValid(Object value) {
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
      final int fractionPartLength = Math.max(bigNum.scale(), 0);

      return (integer >= integerPartLength) && (fraction >= fractionPartLength);
    }
  }

  private static final class PositiveAdapter extends AbstractConstraintAdapter<Object> {

    private final boolean inclusive;

    PositiveAdapter(ValidationContext.Message message, Set<Class<?>> groups, boolean inclusive) {

      super(message, groups);
      this.inclusive = inclusive;
    }

    @Override
    public boolean isValid(Object value) {
      // null values are valid
      if (value == null) {
        return true;
      }

      final int sign = NumberSignHelper.signum(value, LESS_THAN);

      return !(inclusive ? sign < 0 : sign <= 0);
    }
  }

  private static final class NegativeAdapter extends AbstractConstraintAdapter<Object> {

    private final boolean inclusive;

    NegativeAdapter(ValidationContext.Message message, Set<Class<?>> groups, boolean inclusive) {

      super(message, groups);
      this.inclusive = inclusive;
    }

    @Override
    public boolean isValid(Object value) {
      // null values are valid
      if (value == null) {
        return true;
      }

      final int sign = NumberSignHelper.signum(value, GREATER_THAN);

      return !(inclusive ? sign > 0 : sign >= 0);
    }
  }

  private static final class RangeAdapter implements ValidationAdapter<Object> {

    private final ValidationAdapter<Number> adapter;

    RangeAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {

      final var min = (int) attributes.get("min");
      final var max = (int) attributes.get("max");
      this.adapter =
          new MaxAdapter(message, groups, max).andThen(new MinAdapter(message, groups, min));
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      if (value instanceof final String s) {
        value = Long.parseLong(s);
      }

      return adapter.validate((Number) value, req);
    }
  }
}
