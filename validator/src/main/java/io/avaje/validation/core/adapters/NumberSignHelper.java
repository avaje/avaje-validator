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

  static BigDecimal getBigDecimalValue(Object value) {
    BigDecimal bd;
    try {
      bd = new BigDecimal(value.toString());
    } catch (final NumberFormatException nfe) {
      throw new IllegalArgumentException("Object: " + value + " Is not a valid number", nfe);
    }
    return bd;
  }

  static int signum(Object value, OptionalInt treatNanAs) {
    if (value instanceof CharSequence) {
      return signum(getBigDecimalValue(value), treatNanAs);
    } else if (value instanceof final Number number) {
      return signum(number, treatNanAs);
    }
    throw new IllegalArgumentException("Object: " + value + " Is not a valid number");
  }

  private static int signum(Number number, OptionalInt treatNanAs) {
    if (number instanceof final BigDecimal bd) {
      return bd.signum();
    } else if (number instanceof final BigInteger bi) {
      return bi.signum();
    } else if (number instanceof final Short sh) {
      return sh.compareTo(SHORT_ZERO);
    } else if (number instanceof final Integer i) {
      return Integer.signum(i);
    } else if (number instanceof final Long l) {
      return Long.signum(l);
    } else if (number instanceof final Float f) {
      return signum(f, treatNanAs);
    } else if (number instanceof final Double d) {
      return signum(d, treatNanAs);
    } else if (number instanceof final Byte b) {
      return b.compareTo(BYTE_ZERO);
    } else {
      return Double.compare(number.doubleValue(), DOUBLE_ZERO);
    }
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
