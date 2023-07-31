package io.avaje.validation.core.adapters;

import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.GREATER_THAN;
import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.LESS_THAN;

import java.math.BigDecimal;
import java.util.Optional;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

public final class NumberAdapters {
  private NumberAdapters() {}

  public static final ValidationContext.AnnotationFactory FACTORY =
      request->switch (request.annotationType().getSimpleName()) {
    case "Digits" -> new DigitsAdapter(request);
    case "Positive" -> new PositiveAdapter(request, false);
    case "PositiveOrZero" -> new PositiveAdapter(request, true);
    case "Negative" -> new NegativeAdapter(request, false);
    case "NegativeOrZero" -> new NegativeAdapter(request, true);
    case "Max" -> new MaxAdapter(request);
    case "Min" -> new MinAdapter(request);
    case "DecimalMax" -> new DecimalMaxAdapter(request);
    case "DecimalMin" -> new DecimalMinAdapter(request);
    case "Range" -> new RangeAdapter(request);
    default -> null;
  };

  private static final class DecimalMaxAdapter extends AbstractConstraintAdapter<Number> {

    private final BigDecimal value;
    private final boolean inclusive;

    DecimalMaxAdapter(AdapterCreateRequest request) {
      super(request);
      final var attributes = request.attributes();
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

    DecimalMinAdapter(AdapterCreateRequest request) {
      super(request);
      final var attributes = request.attributes();
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

    MaxAdapter(AdapterCreateRequest request) {
      super(request);
      final var attributes = request.attributes();
      this.value = (long) attributes.get("value");
    }

    MaxAdapter(AdapterCreateRequest request, long value) {
      super(request);
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

    MinAdapter(AdapterCreateRequest request) {
      super(request);
      final var attributes = request.attributes();
      this.value = (long) attributes.get("value");
    }

    MinAdapter(AdapterCreateRequest request, long value) {
      super(request);
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

    DigitsAdapter(AdapterCreateRequest request) {
      super(request);
      final var attributes = request.attributes();
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

    PositiveAdapter(AdapterCreateRequest request, boolean inclusive) {
      super(request);
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

    NegativeAdapter(AdapterCreateRequest request, boolean inclusive) {
      super(request);
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

  private static final class RangeAdapter extends AbstractConstraintAdapter<Object> {

    private final MaxAdapter maxAdapter;
    private final MinAdapter minAdapter;

    RangeAdapter(AdapterCreateRequest request) {
      super(request);
      final var attributes = request.attributes();
      final var min = (long) attributes.get("min");
      final var max = (long) attributes.get("max");
      this.maxAdapter = new MaxAdapter(request, max);
      this.minAdapter = new MinAdapter(request, min);
    }

    @Override
    public boolean isValid(Object value) {

      if (value instanceof final String s) {
        value = Long.parseLong(s);
      }
      final var num = (Number) value;
      return minAdapter.isValid(num) && maxAdapter.isValid(num);
    }
  }
}
