package io.avaje.validation.core.adapters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;

final class NumberComparatorHelper {

  private NumberComparatorHelper() {}

  static int compareDecimal(String targetType, Number number, BigDecimal value, OptionalInt treatNanAs) {
    return switch (targetType) {
      case "Double" -> compare((Double) number, value, treatNanAs);
      case "Float" -> compare((Float) number, value, treatNanAs);
      case "BigDecimal" -> ((BigDecimal) number).compareTo(value);
      case "BigInteger" -> new BigDecimal((BigInteger) number).compareTo(value);
      case "Byte", "Integer", "Long", "Short" -> BigDecimal.valueOf(number.longValue()).compareTo(value);
      default -> compare(number.doubleValue(), value, treatNanAs);
    };
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
