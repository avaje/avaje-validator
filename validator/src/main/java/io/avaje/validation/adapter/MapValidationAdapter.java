package io.avaje.validation.adapter;

import java.util.Map;

final class MapValidationAdapter<T> extends ContainerAdapter<T> {

  private final boolean keys;

  MapValidationAdapter(ValidationAdapter<T> adapters, boolean keys) {
    super(adapters);
    this.keys = keys;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean validate(T value, ValidationRequest req, String propertyName) {
    final var map = (Map<Object, Object>) value;

    if (initalAdapter.validate(value, req, propertyName)) {
      if (keys) {
        return validateAll(map.keySet(), req, propertyName);
      }
      return validateAll(map.values(), req, propertyName);
    }

    return true;
  }
}
