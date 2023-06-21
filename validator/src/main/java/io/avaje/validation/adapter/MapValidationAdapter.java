package io.avaje.validation.adapter;

import java.util.Map;

class MapValidationAdapter extends AbstractMultiAdapter {

  private final boolean keys;

  public MapValidationAdapter(ValidationAdapter<?> adapters, boolean keys) {
    super(adapters);
    this.keys = keys;
  }

  @Override
  public boolean validate(Object value, ValidationRequest req, String propertyName) {
    final var map = (Map<Object, Object>) value;
    if (keys) {
      return validateAll(map.keySet(), req, propertyName);
    }
    return validateAll(map.values(), req, propertyName);
  }
}
