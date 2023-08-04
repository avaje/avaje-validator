package io.avaje.validation.core.adapters;

import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.GREATER_THAN;
import static io.avaje.validation.core.adapters.InfinityNumberComparatorHelper.LESS_THAN;
import static io.avaje.validation.core.adapters.NumberComparatorHelper.compareDouble;
import static io.avaje.validation.core.adapters.NumberComparatorHelper.compareFloat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

public final class NumberAdapters {
  private NumberAdapters() {}

  public static final ValidationContext.AnnotationFactory FACTORY =
    request -> switch (request.annotationType().getSimpleName()) {
    case "Digits" -> new DigitsAdapter(request);
    case "Positive" -> new PositiveAdapter(request, false);
    case "PositiveOrZero" -> new PositiveAdapter(request, true);
    case "Negative" -> new NegativeAdapter(request, false);
    case "NegativeOrZero" -> new NegativeAdapter(request, true);
    case "Max" -> max(request);
    case "Min" -> min(request);
    case "DecimalMax" -> new DecimalMaxAdapter(request);
    case "DecimalMin" -> new DecimalMinAdapter(request);
    case "Range" -> range(request);
    default -> null;
  };

  private static ValidationAdapter<?> range(AdapterCreateRequest request) {
    if ("String".equals(request.targetType())) {
      return new RangeStringAdapter(request);
    } else {
      return new RangeAdapter(request);
    }
  }

  private static AbstractConstraintAdapter<? extends Number> max(AdapterCreateRequest request) {
    return switch (request.targetType()) {
      case "BigDecimal" -> new MaxBigDecimal(request);
      case "BigInteger" -> new MaxBigInteger(request);
      default -> new MaxAdapter(request);
    };
  }

  private static AbstractConstraintAdapter<? extends Number> min(AdapterCreateRequest request) {
    final String targetType = request.targetType();
    return switch (targetType) {
      case "BigDecimal" -> new MinBigDecimal(request);
      case "BigInteger" -> new MinBigInteger(request);
      default -> new MinAdapter(request);
    };
  }

  private static final class DecimalMaxAdapter extends AbstractConstraintAdapter<Number> {

    private final BigDecimal value;
    private final boolean inclusive;
    private final String targetType;

    DecimalMaxAdapter(AdapterCreateRequest request) {
      super(request);
      final var attributes = request.attributes();
      this.value = new BigDecimal((String) attributes.get("value"));
      this.inclusive = Optional.ofNullable((Boolean) attributes.get("inclusive")).orElse(true);
      this.targetType = request.targetType();
    }

    @Override
    public boolean isValid(Number number) {
      // null values are valid
      if (number == null) {
        return true;
      }
      final int comparisonResult = NumberComparatorHelper.compareDecimal(targetType, number, value, LESS_THAN);
      return !(inclusive ? comparisonResult > 0 : comparisonResult >= 0);
    }
  }

  private static final class DecimalMinAdapter extends AbstractConstraintAdapter<Number> {

    private final BigDecimal value;
    private final boolean inclusive;
    private final String targetType;

    DecimalMinAdapter(AdapterCreateRequest request) {
      super(request);
      final var attributes = request.attributes();
      this.value = new BigDecimal((String) attributes.get("value"));
      this.inclusive = Optional.ofNullable((Boolean) attributes.get("inclusive")).orElse(true);
      this.targetType = request.targetType();
    }

    @Override
    public boolean isValid(Number number) {
      // null values are valid
      if (number == null) {
        return true;
      }
      final int comparisonResult = NumberComparatorHelper.compareDecimal(targetType, number, value, LESS_THAN);
      return !(inclusive ? comparisonResult < 0 : comparisonResult <= 0);
    }
  }

  public interface NumberAdapter<T extends Number> {
    boolean isValid(T number);
  }

  private static final class MaxAdapter extends AbstractConstraintAdapter<Number> implements NumberAdapter<Number> {

    private final long value;
    private final String targetType;

    MaxAdapter(AdapterCreateRequest request) {
      super(request);
      this.targetType = request.targetType();
      this.value = (long) request.attribute("value");
    }

    @Override
    public boolean isValid(Number number) {
      // null values are valid
      if (number == null) {
        return true;
      }
      return switch (targetType) {
        case "Integer", "Long", "Short", "Byte" -> number.longValue() <= value;
        case "Double", "Number" -> compareDouble(number.doubleValue(), value, GREATER_THAN)  <= 0;
        case "Float" -> compareFloat((Float)number, value, GREATER_THAN)  <= 0;
        default -> throw new IllegalStateException();
      };
    }
  }

  static final class MaxBigDecimal extends AbstractConstraintAdapter<BigDecimal> implements NumberAdapter<BigDecimal> {

    private final BigDecimal max;

    MaxBigDecimal(AdapterCreateRequest request) {
      super(request);
      this.max = new BigDecimal(String.valueOf(request.attribute("value")));
    }

    @Override
    public boolean isValid(BigDecimal number) {
      return number == null || number.compareTo(max) <= 0;
    }
  }

  static final class MaxBigInteger extends AbstractConstraintAdapter<BigInteger> implements NumberAdapter<BigInteger> {

    private final BigInteger max;

    MaxBigInteger(AdapterCreateRequest request) {
      super(request);
      this.max = new BigInteger(String.valueOf(request.attribute("value")));
    }

    @Override
    public boolean isValid(BigInteger number) {
      return number == null || number.compareTo(max) <= 0;
    }
  }

  private static final class MinAdapter extends AbstractConstraintAdapter<Number> implements NumberAdapter<Number> {

    private final long value;
    private final String targetType;

    MinAdapter(AdapterCreateRequest request) {
      super(request);
      this.targetType = request.targetType();
      this.value = (long) request.attribute("value");
    }

    @Override
    public boolean isValid(Number number) {
      if (number == null) {
        return true;
      }
      return switch (targetType) {
        case "Integer", "Long", "Short", "Byte" -> number.longValue() >= value;
        case "Double" -> compareDouble(number.doubleValue(), value, LESS_THAN)  >= 0;
        case "Float" -> compareFloat((Float)number, value, LESS_THAN)  >= 0;
        default -> throw new IllegalStateException();
      };
    }
  }

  static final class MinBigDecimal extends AbstractConstraintAdapter<BigDecimal> implements NumberAdapter<BigDecimal> {

    private final BigDecimal min;

    MinBigDecimal(AdapterCreateRequest request) {
      super(request);
      this.min = new BigDecimal(String.valueOf(request.attribute("value")));
    }

    @Override
    public boolean isValid(BigDecimal number) {
      return number == null || number.compareTo(min) >= 0;
    }
  }

  static final class MinBigInteger extends AbstractConstraintAdapter<BigInteger> implements NumberAdapter<BigInteger> {

    private final BigInteger min;

    MinBigInteger(AdapterCreateRequest request) {
      super(request);
      this.min = new BigInteger(String.valueOf(request.attribute("value")));
    }

    @Override
    public boolean isValid(BigInteger number) {
      return number == null || number.compareTo(min) >= 0;
    }
  }

  private static final class DigitsAdapter extends AbstractConstraintAdapter<Object> {

    private final int integer;
    private final int fraction;

    DigitsAdapter(AdapterCreateRequest request) {
      super(request);
      this.integer = (int) request.attribute("integer");
      this.fraction = (int) request.attribute("fraction");
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
        bigNum = NumberSignHelper.toBigDecimal(value.toString()).stripTrailingZeros();
      }

      final int integerPartLength = bigNum.precision() - bigNum.scale();
      final int fractionPartLength = Math.max(bigNum.scale(), 0);
      return (integer >= integerPartLength) && (fraction >= fractionPartLength);
    }
  }

  private static final class PositiveAdapter extends AbstractConstraintAdapter<Object> {

    private final boolean inclusive;
    private final String targetType;

    PositiveAdapter(AdapterCreateRequest request, boolean inclusive) {
      super(request);
      this.inclusive = inclusive;
      this.targetType = request.targetType();
    }

    @Override
    public boolean isValid(Object value) {
      // null values are valid
      if (value == null) {
        return true;
      }
      final int sign = NumberSignHelper.signum(targetType, value, LESS_THAN);
      return !(inclusive ? sign < 0 : sign <= 0);
    }
  }

  private static final class NegativeAdapter extends AbstractConstraintAdapter<Object> {

    private final boolean inclusive;
    private final String targetType;

    NegativeAdapter(AdapterCreateRequest request, boolean inclusive) {
      super(request);
      this.inclusive = inclusive;
      this.targetType = request.targetType();
    }

    @Override
    public boolean isValid(Object value) {
      // null values are valid
      if (value == null) {
        return true;
      }
      final int sign = NumberSignHelper.signum(targetType, value, GREATER_THAN);
      return !(inclusive ? sign > 0 : sign >= 0);
    }
  }

  private static final class RangeAdapter extends AbstractConstraintAdapter<Number> {

    private final NumberAdapter<Number> maxAdapter;
    private final NumberAdapter<Number> minAdapter;

    @SuppressWarnings("unchecked")
    RangeAdapter(AdapterCreateRequest request) {
      super(request);
      final var min = (long) request.attribute("min");
      final var max = (long) request.attribute("max");
      this.maxAdapter = (NumberAdapter<Number>) max(request.withValue(max));
      this.minAdapter = (NumberAdapter<Number>) min(request.withValue(min));
    }

    @Override
    public boolean isValid(Number value) {
      if (value == null) {
        return true;
      }
      return minAdapter.isValid(value) && maxAdapter.isValid(value);
    }
  }

  private static final class RangeStringAdapter extends AbstractConstraintAdapter<Object> {

    private final BigDecimal min;
    private final BigDecimal max;

    @SuppressWarnings("unchecked")
    RangeStringAdapter(AdapterCreateRequest request) {
      super(request);
      this.min = BigDecimal.valueOf((long) request.attribute("min"));
      this.max = BigDecimal.valueOf((long) request.attribute("max"));
    }

    @Override
    public boolean isValid(Object value) {
      if (value == null) {
        return true;
      }
      final var decimal = new BigDecimal(value.toString());
      return min.compareTo(decimal) <= 0 && max.compareTo(decimal) >= 0;
    }
  }
}
