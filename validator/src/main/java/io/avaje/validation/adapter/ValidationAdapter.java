package io.avaje.validation.adapter;

import java.util.Objects;
import java.util.Set;

@FunctionalInterface
public interface ValidationAdapter<T> {

  /** Return true if validation should recurse */
  boolean validate(T value, ValidationRequest req, String propertyName);

  default boolean validate(T value, ValidationRequest req) {
    return validate(value, req, null);
  }

  default AbstractContainerAdapter<T> list() {

    return new CollectionValidationAdapter<>(this);
  }

  default AbstractContainerAdapter<T> mapKeys() {
    return new MapValidationAdapter<>(this, true);
  }

  default AbstractContainerAdapter<T> mapValues() {
    return new MapValidationAdapter<>(this, false);
  }

  default AbstractContainerAdapter<T> array() {
    return new ArrayValidationAdapter<>(this);
  }

  default AbstractContainerAdapter<T> optional() {
    return new OptionalValidationAdapter<>(this);
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

  /**
   * Returns true if the validation request groups is empty or matches any of the adapter's
   * configured groups
   */
  default boolean checkGroups(Set<Class<?>> adapterGroups, ValidationRequest request) {
    final var requestGroups = request.groups();

    if (requestGroups.isEmpty()) return true;

    for (final var group : requestGroups) {
      if (adapterGroups.contains(group)) {
        return true;
      }
    }
    return false;
  }
}
