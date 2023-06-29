package io.avaje.validation.adapter;

class ArrayValidationAdapter<T> extends AbstractMultiAdapter<T> {

  public ArrayValidationAdapter(ValidationAdapter<T> adapters) {
    super(adapters);
  }

  @Override
  public boolean validate(Object value, ValidationRequest req, String propertyName) {
    if (validate(value, req, propertyName)) {

      return validateArray((Object[]) value, req, propertyName);
    }
    return true;
  }
}
