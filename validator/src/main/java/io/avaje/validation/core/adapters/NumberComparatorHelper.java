package io.avaje.validation.core.adapters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;

final class NumberComparatorHelper {

  private NumberComparatorHelper() {}

  static int compareDecimal(String targetType, Object number, BigDecimal value, OptionalInt treatNanAs) {
    if (targetType == null) {
      return compareDecimal(number, value, treatNanAs);
    }
    return switch (targetType) {
      case "String", "CharSequence" -> new BigDecimal(number.toString()).compareTo(value);
      case "Double" -> compareDouble((Double) number, value, treatNanAs);
      case "Float" -> compareFloat((Float) number, value, treatNanAs);
      case "BigDecimal" -> ((BigDecimal) number).compareTo(value);
      case "BigInteger" -> new BigDecimal((BigInteger) number).compareTo(value);
      case "Byte", "Integer", "Long", "Short" -> BigDecimal.valueOf(((Number) number).longValue())
          .compareTo(value);
      default -> compareDecimal(number, value, treatNanAs);
    };
  }

  private static int compareDecimal(Object number, BigDecimal value, OptionalInt treatNanAs) {
    if (number instanceof Number n) {
      return compareDouble(n.doubleValue(), value, treatNanAs);
    }
    return new BigDecimal(number.toString()).compareTo(value);
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

  private static int compareDouble(Double number, BigDecimal value, OptionalInt treatNanAs) {
    final OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
    if (infinity.isPresent()) {
      return infinity.getAsInt();
    }
    return BigDecimal.valueOf(number).compareTo(value);
  }

  private static int compareFloat(Float number, BigDecimal value, OptionalInt treatNanAs) {
    final OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
    if (infinity.isPresent()) {
      return infinity.getAsInt();
    }
    return BigDecimal.valueOf(number).compareTo(value);
  }
}
