package io.avaje.validation.adapter;

final class ArrayValidationAdapter<T> extends ContainerAdapter<T> {

  ArrayValidationAdapter(ValidationAdapter<T> adapters) {
    super(adapters);
  }

  @Override
  public boolean validate(T value, ValidationRequest req, String propertyName) {
    if (initalAdapter.validate(value, req, propertyName)) {

      return validateArray((Object[]) value, req, propertyName);
    }
    return true;
  }
}
