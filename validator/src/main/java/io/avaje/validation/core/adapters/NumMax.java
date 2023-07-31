package io.avaje.validation.core.adapters;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

import java.math.BigDecimal;

class NumMax {

  static abstract class MaxIntegerBase<T> extends AbstractConstraintAdapter<T> {

    protected final long value;

    MaxIntegerBase(AdapterCreateRequest request) {
      super(request);
      final var attributes = request.attributes();
      this.value = (long) attributes.get("value");
    }

    MaxIntegerBase(AdapterCreateRequest request, long value) {
      super(request);
      this.value = value;
    }
  }

  static final class IntegerAdapter extends MaxIntegerBase<Integer> {

    IntegerAdapter(AdapterCreateRequest request) {
      super(request);
    }

    IntegerAdapter(AdapterCreateRequest request, long value) {
      super(request, value);
    }

    @Override
    public boolean isValid(Integer number) {
      // null values are valid
      return number == null || number <= value;
    }
  }

  static final class LongAdapter extends MaxIntegerBase<Long> {

    LongAdapter(AdapterCreateRequest request) {
      super(request);
    }

    LongAdapter(AdapterCreateRequest request, long value) {
      super(request, value);
    }

    @Override
    public boolean isValid(Long number) {
      // null values are valid
      return number == null || number <= value;
    }
  }

  static final class BigDecimalAdapter extends MaxIntegerBase<BigDecimal> {

    private final BigDecimal bdMax;
    BigDecimalAdapter(AdapterCreateRequest request) {
      super(request);
      this.bdMax = BigDecimal.valueOf(value);
    }

    BigDecimalAdapter(AdapterCreateRequest request, long value) {
      super(request, value);
      this.bdMax = BigDecimal.valueOf(value);
    }

    @Override
    public boolean isValid(BigDecimal number) {
      // null values are valid
      return number == null || number.compareTo(bdMax) <= 0;
    }
  }
}
