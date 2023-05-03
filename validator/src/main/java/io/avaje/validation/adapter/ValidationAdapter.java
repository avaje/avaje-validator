package io.avaje.validation.adapter;

import java.util.Collection;
import java.util.Objects;

public interface ValidationAdapter<T> {

  /** Return true if validation should recurse */
  boolean validate(T value, ValidationRequest req, String propertyName);

  default boolean validate(T value, ValidationRequest req) {
    return validate(value, req, null);
  }

  default boolean validateAll(Collection<T> value, ValidationRequest req, String propertyName) {
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = -1;
    for (final T element : value) {
      index++;
      validate(element, req, String.valueOf(index));
    }
    if (propertyName != null) {
      req.popPath();
    }
    return true;
  }

  default boolean validateAll(T[] value, ValidationRequest req, String propertyName) {
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = -1;
    for (final T element : value) {
      index++;
      validate(element, req, String.valueOf(index));
    }
    if (propertyName != null) {
      req.popPath();
    }
    return true;
  }

  default ValidationAdapter<T> andThen(ValidationAdapter<? super T> after) {
    Objects.requireNonNull(after);
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        return after.validate(value, req, propertyName);
      }
      return true;
    };
  }

}
