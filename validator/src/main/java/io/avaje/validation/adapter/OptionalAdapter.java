package io.avaje.validation.adapter;

import java.util.Optional;

final class OptionalAdapter<T> implements ValidationAdapter<Optional<T>> {

  private ValidationAdapter<T> initalAdapter;

  OptionalAdapter(ValidationAdapter<T> initalAdapter) {
    this.initalAdapter = initalAdapter;
  }

  @Override
  public boolean validate(Optional<T> value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }

    value.ifPresent(v -> initalAdapter.validate(v, req, propertyName));
    return true;
  }
}
