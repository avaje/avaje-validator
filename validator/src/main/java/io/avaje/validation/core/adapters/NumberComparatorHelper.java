package io.avaje.validation.core.adapters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;

final class NumberComparatorHelper {

  private NumberComparatorHelper() {}

  static int compare(Number number, long value, OptionalInt treatNanAs) {
    if (number instanceof final Double d) {
      return compareDouble(d, value, treatNanAs);
    } else if (number instanceof final Float f) {
      return compareFloat(f, value, treatNanAs);
    } else if (number instanceof final BigDecimal bd) {
      return bd.compareTo(BigDecimal.valueOf(value));
    } else if (number instanceof final BigInteger bi) {
      return bi.compareTo(BigInteger.valueOf(value));
    } else if (number instanceof Byte
        || number instanceof Integer
        || number instanceof Long
        || number instanceof Short) {
      final Long numLong = number.longValue();
      return numLong.compareTo(value);
    }
    return compare(number.doubleValue(), value, treatNanAs);
  }

  static int compareDecimal(Number number, BigDecimal value, OptionalInt treatNanAs) {
    if (number instanceof final Double d) {
      return compare(d, value, treatNanAs);
    } else if (number instanceof final Float f) {
      return compare(f, value, treatNanAs);
    } else if (number instanceof final BigDecimal bd) {
      return bd.compareTo(value);
    } else if (number instanceof final BigInteger bi) {
      return new BigDecimal(bi).compareTo(value);
    } else if (number instanceof Byte
        || number instanceof Integer
        || number instanceof Long
        || number instanceof Short) {

      return BigDecimal.valueOf(number.longValue()).compareTo(value);
    }

    return compare(number.doubleValue(), value, treatNanAs);
  }

  static int compareDouble(Double number, long value, OptionalInt treatNanAs) {
    final OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
    if (infinity.isPresent()) {
      return infinity.getAsInt();
    }
    return Double.compare(number, value);
  }

  static int compareFloat(Float number, long value, OptionalInt treatNanAs) {
    final OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
    if (infinity.isPresent()) {
      return infinity.getAsInt();
    }
    return Float.compare(number, value);
  }

  private static int compare(Double number, BigDecimal value, OptionalInt treatNanAs) {
    final OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
    if (infinity.isPresent()) {
      return infinity.getAsInt();
    }
    return BigDecimal.valueOf(number).compareTo(value);
  }

  private static int compare(Float number, BigDecimal value, OptionalInt treatNanAs) {
    final OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
    if (infinity.isPresent()) {
      return infinity.getAsInt();
    }
    return BigDecimal.valueOf(number).compareTo(value);
  }
}
