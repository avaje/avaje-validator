package io.avaje.validation.adapter;

final class IterableValidationAdapter<T> extends AbstractContainerAdapter<T> {

  IterableValidationAdapter(ValidationAdapter<T> adapters) {
    super(adapters);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean validate(T value, ValidationRequest req, String propertyName) {
    if (initalAdapter.validate(value, req, propertyName)) {
      return validateAll((Iterable<Object>) value, req, propertyName);
    }

    return true;
  }
}
