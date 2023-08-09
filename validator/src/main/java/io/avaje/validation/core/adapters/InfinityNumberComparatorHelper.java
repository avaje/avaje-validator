package io.avaje.validation.core.adapters;

import java.util.OptionalInt;

/** @author Marko Bekhta */
final class InfinityNumberComparatorHelper {

  static final OptionalInt LESS_THAN = OptionalInt.of(-1);
  static final OptionalInt FINITE_VALUE = OptionalInt.empty();
  static final OptionalInt GREATER_THAN = OptionalInt.of(1);

  private InfinityNumberComparatorHelper() {}

  static OptionalInt infinityCheck(Double number, OptionalInt treatNanAs) {
    OptionalInt result = FINITE_VALUE;
    if (number == Double.NEGATIVE_INFINITY) {
      result = LESS_THAN;
    } else if (number.isNaN()) {
      result = treatNanAs;
    } else if (number == Double.POSITIVE_INFINITY) {
      result = GREATER_THAN;
    }
    return result;
  }

  static OptionalInt infinityCheck(Float number, OptionalInt treatNanAs) {
    OptionalInt result = FINITE_VALUE;
    if (number == Float.NEGATIVE_INFINITY) {
      result = LESS_THAN;
    } else if (number.isNaN()) {
      result = treatNanAs;
    } else if (number == Float.POSITIVE_INFINITY) {
      result = GREATER_THAN;
    }
    return result;
  }
}
