package io.avaje.validation.adapter;

final class NullSafeAdapter<T> implements ValidationAdapter<T> {

  private final ValidationAdapter<T> delegate;

  NullSafeAdapter(ValidationAdapter<T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public String toString() {
    return delegate + ".nullSafe()";
  }

  @Override
  public boolean validate(T value, ValidationRequest req, String propertyName) {

    return value != null && delegate.validate(value, req, propertyName);
  }
}
