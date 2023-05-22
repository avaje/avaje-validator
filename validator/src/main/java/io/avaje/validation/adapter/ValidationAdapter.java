package io.avaje.validation.adapter;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public interface ValidationAdapter<T> {

  /** Return true if validation should recurse */
  boolean validate(T value, ValidationRequest req, String propertyName);

  default boolean validate(T value, ValidationRequest req) {
    return validate(value, req, null);
  }

  default ValidationAdapter<T> list(ValidationContext ctx, Class<?> clazz) {
    final var after = ctx.<Object>adapter(clazz);
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        return after.validateAll((Collection<Object>) value, req, propertyName);
      }
      return true;
    };
  }

  default ValidationAdapter<T> map(ValidationContext ctx, Class<?> clazz) {
    final var after = ctx.<Object>adapter(clazz);
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        final var map = (Map<?, Object>) value;
        return after.validateAll(map.values(), req, propertyName);
      }
      return true;
    };
  }

  default ValidationAdapter<T> array(ValidationContext ctx, Class<?> clazz) {
    final var after = ctx.<Object>adapter(clazz);
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        return after.validateArray((Object[]) value, req, propertyName);
      }
      return true;
    };
  }

  private boolean validateAll(Collection<T> value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = -1;
    for (final var element : value) {
      index++;
      validate(element, req, String.valueOf(index));
    }
    if (propertyName != null) {
      req.popPath();
    }
    return true;
  }

  private boolean validateArray(T[] value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }
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
