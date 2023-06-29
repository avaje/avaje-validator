package io.avaje.validation.adapter;

import java.util.Map;

class MapValidationAdapter<T> extends AbstractMultiAdapter<T> {

  private final boolean keys;

  public MapValidationAdapter(ValidationAdapter<T> adapters, boolean keys) {
    super(adapters);
    this.keys = keys;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean validate(Object value, ValidationRequest req, String propertyName) {
    final var map = (Map<Object, Object>) value;

    if (validate(value, req, propertyName)) {
      if (keys) {
        return validateAll(map.keySet(), req, propertyName);
      }
      return validateAll(map.values(), req, propertyName);
    }

    return true;
  }
}
