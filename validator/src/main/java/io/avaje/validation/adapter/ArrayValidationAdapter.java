package io.avaje.validation.adapter;

class ArrayValidationAdapter<T> extends AbstractMultiAdapter<T> {

  ArrayValidationAdapter(ValidationAdapter<T> adapters) {
    super(adapters);
  }

  @Override
  public boolean validate(T value, ValidationRequest req, String propertyName) {
    if (starterAdapter.validate(value, req, propertyName)) {

      return validateArray((Object[]) value, req, propertyName);
    }
    return true;
  }
}
