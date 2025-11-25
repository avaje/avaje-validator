package io.avaje.validation.adapter;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

final class PrimitiveOptional<T> implements ValidationAdapter<T> {

  private ValidationAdapter<T> initalAdapter;

  PrimitiveOptional(ValidationAdapter<T> initalAdapter) {
    this.initalAdapter = initalAdapter;
  }

  @Override
  public boolean validate(T value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }
    if (value instanceof final OptionalInt i) {
      i.ifPresent(v -> initalAdapter.validate((T) (Integer) v, req, propertyName));
    } else if (value instanceof final OptionalLong l) {
      l.ifPresent(v -> initalAdapter.validate((T) (Long) v, req, propertyName));
    } else if (value instanceof final OptionalDouble d) {
      d.ifPresent(v -> initalAdapter.validate((T) (Double) v, req, propertyName));
    }
    return true;
  }
}
