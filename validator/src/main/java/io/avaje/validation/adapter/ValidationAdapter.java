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

  default ValidationAdapter<T> list(ValidationAdapter<?> adapter) {
    final var coll = new CollectionValidationAdapter(adapter);
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        return coll.validate(value, req);
      }
      return true;
    };
  }

  default ValidationAdapter<T> map(ValidationAdapter<?> adapter) {
    final var coll = new MapValidationAdapter(adapter, false);
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        return coll.validate(value, req);
      }
      return true;
    };
  }

  default ValidationAdapter<T> array(ValidationAdapter<?> adapter) {
    final var coll = new ArrayValidationAdapter(adapter);
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        return coll.validate(value, req);
      }
      return true;
    };
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
