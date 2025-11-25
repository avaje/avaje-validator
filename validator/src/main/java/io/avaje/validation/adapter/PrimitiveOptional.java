package io.avaje.validation.adapter;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

final class PrimitiveOptional<T> extends ContainerAdapter<T> {

  PrimitiveOptional(ValidationAdapter<T> adapter) {
    super(adapter);
  }

  @Override
  @SuppressWarnings("unchecked")
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
