package io.avaje.validation.adapter;

import java.util.Optional;

final class OptionalAdapter<T> implements ValidationAdapter<Optional<T>> {

  private final ValidationAdapter<T> adapter;

  OptionalAdapter(ValidationAdapter<T> adapter) {
    this.adapter = adapter;
  }

  @Override
  public boolean validate(Optional<T> value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }

    value.ifPresent(v -> adapter.validate(v, req, propertyName));
    return true;
  }
}
