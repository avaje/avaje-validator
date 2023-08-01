package io.avaje.validation.core.adapters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;

final class NumberSignHelper {

  private NumberSignHelper() {}

  private static final short SHORT_ZERO = (short) 0;
  private static final float FLOAT_ZERO = 0f;
  private static final double DOUBLE_ZERO = 0D;
  private static final byte BYTE_ZERO = (byte) 0;

  static BigDecimal toBigDecimal(Object value) {
    try {
      return new BigDecimal(value.toString());
    } catch (final NumberFormatException nfe) {
      throw new IllegalArgumentException("Object: " + value + " Is not a valid number", nfe);
    }
  }

  static int signum(String targetType, Object value, OptionalInt treatNanAs) {
    if (targetType == null) {
      return Double.compare(((Number) value).doubleValue(), DOUBLE_ZERO);
    }
    return switch (targetType) {
      case "String", "CharSequence" -> toBigDecimal(value).signum();
      case "BigDecimal" -> ((BigDecimal) value).signum();
      case "BigInteger" -> ((BigInteger) value).signum();
      case "Byte" -> ((Byte) value).compareTo(BYTE_ZERO);
      case "Short" -> ((Short) value).compareTo(SHORT_ZERO);
      case "Integer" -> Integer.signum((Integer) value);
      case "Long" -> Long.signum((Long) value);
      case "Float" -> signum((Float) value, treatNanAs);
      case "Double" -> signum((Double) value, treatNanAs);
      default -> Double.compare(((Number) value).doubleValue(), DOUBLE_ZERO);
    };
  }

  static int signum(Float number, OptionalInt treatNanAs) {
    final OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
    if (infinity.isPresent()) {
      return infinity.getAsInt();
    }
    return number.compareTo(FLOAT_ZERO);
  }

  static int signum(Double number, OptionalInt treatNanAs) {
    final OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
    if (infinity.isPresent()) {
      return infinity.getAsInt();
    }
    return number.compareTo(DOUBLE_ZERO);
  }
}
