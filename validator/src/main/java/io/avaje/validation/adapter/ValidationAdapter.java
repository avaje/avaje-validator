package io.avaje.validation.adapter;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

import io.avaje.validation.Validator;

public interface ValidationAdapter<T> {

  /** Return true if validation should recurse */
  boolean validate(T value, ValidationRequest req, String propertyName);

  default boolean validate(T value, ValidationRequest req) {
    return validate(value, req, null);
  }

  default boolean validateAll(Collection<T> value, ValidationRequest req, String propertName) {
    if (propertName != null) {
      req.pushPath(propertName);
    }
    int index = -1;
    for (final T element : value) {
      index++;
  validate(element, req, String.valueOf(index));
    }
    if (propertName != null) {
      req.popPath();
    }
    return true;
  }

  default boolean validateAll(T[] value, ValidationRequest req, String propertName) {
    if (propertName != null) {
      req.pushPath(propertName);
    }
    int index = -1;
    for (final T element : value) {
      index++;
  validate(element, req, String.valueOf(index));
    }
    if (propertName != null) {
      req.popPath();
    }
    return true;
  }

  default AnnotationValidationAdapter<T> andThen(ValidationAdapter<? super T> after) {
    Objects.requireNonNull(after);
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        return after.validate(value, req, propertyName);
      }
      return true;
    };
  }

  /** Factory for creating a ValidationAdapter. */
  public interface Factory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     *
     * <p>Returning null means that the adapter could be created by another factory.
     */
    ValidationAdapter<?> create(Type type, Validator jsonb);
  }
}
