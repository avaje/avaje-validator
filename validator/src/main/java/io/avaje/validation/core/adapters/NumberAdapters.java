package io.avaje.validation.core.adapters;

import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.GREATER_THAN;
import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.LESS_THAN;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
            default -> null;
          };

  private static final class DecimalMaxAdapter implements ValidationAdapter<Number> {

    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;
    private final BigDecimal value;
    private final boolean inclusive;

    DecimalMaxAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {
      this.message = message;
      this.groups = groups;
      this.value = new BigDecimal((String) attributes.get("value"));
      this.inclusive = Optional.ofNullable((Boolean) attributes.get("inclusive")).orElse(true);
    }

    @Override
    public boolean validate(Number number, ValidationRequest req, String propertyName) {
      // null values are valid
      if (number == null || !checkGroups(groups, req)) {
        return true;
      }

      final int comparisonResult = NumberComparatorHelper.compareDecimal(number, value, LESS_THAN);
      if (inclusive ? comparisonResult > 0 : comparisonResult >= 0) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }

  private static final class DecimalMinAdapter implements ValidationAdapter<Number> {

    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;
    private final BigDecimal value;
    private final boolean inclusive;

    DecimalMinAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {
      this.message = message;
      this.groups = groups;
      this.value = new BigDecimal((String) attributes.get("value"));
      this.inclusive = Optional.ofNullable((Boolean) attributes.get("inclusive")).orElse(true);
    }

    @Override
    public boolean validate(Number number, ValidationRequest req, String propertyName) {
      // null values are valid
      if (number == null || !checkGroups(groups, req)) {
        return true;
      }

      final int comparisonResult = NumberComparatorHelper.compareDecimal(number, value, LESS_THAN);
      if (inclusive ? comparisonResult < 0 : comparisonResult <= 0) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }

  private static final class MaxAdapter implements ValidationAdapter<Number> {

    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;
    private final long value;

    MaxAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {
      this.message = message;
      this.groups = groups;
      this.value = (long) attributes.get("value");
    }

    @Override
    public boolean validate(Number number, ValidationRequest req, String propertyName) {
      // null values are valid
      if (number == null || !checkGroups(groups, req)) {
        return true;
      }

      if (NumberComparatorHelper.compare(number, value, GREATER_THAN) > 0) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }

  private static final class MinAdapter implements ValidationAdapter<Number> {

    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;
    private final long value;

    MinAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {
      this.message = message;
      this.groups = groups;
      this.value = (long) attributes.get("value");
    }

    @Override
    public boolean validate(Number number, ValidationRequest req, String propertyName) {
      // null values are valid
      if (number == null || !checkGroups(groups, req)) {
        return true;
      }

      if (NumberComparatorHelper.compare(number, value, LESS_THAN) < 0) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }

  private static final class DigitsAdapter implements ValidationAdapter<Object> {

    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;
    private final int integer;
    private final int fraction;

    DigitsAdapter(
        ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {
      this.message = message;
      this.groups = groups;
      this.integer = (int) attributes.get("integer");
      this.fraction = (int) attributes.get("fraction");
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      // null values are valid
      if (value == null || !checkGroups(groups, req)) {
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
      if (integer < integerPartLength || fraction < fractionPartLength) {
        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class PositiveAdapter implements ValidationAdapter<Object> {

    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;
    private final boolean inclusive;

    PositiveAdapter(ValidationContext.Message message, Set<Class<?>> groups, boolean inclusive) {
      this.message = message;
      this.groups = groups;
      this.inclusive = inclusive;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      // null values are valid
      if (value == null || !checkGroups(groups, req)) {
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

    private final ValidationContext.Message message;
    private final Set<Class<?>> groups;
    private final boolean inclusive;

    NegativeAdapter(ValidationContext.Message message, Set<Class<?>> groups, boolean inclusive) {
      this.message = message;
      this.groups = groups;
      this.inclusive = inclusive;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {
      // null values are valid
      if (value == null || !checkGroups(groups, req)) {
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
