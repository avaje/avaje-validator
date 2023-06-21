package io.avaje.validation.adapter;

class ArrayValidationAdapter extends AbstractMultiAdapter {

  public ArrayValidationAdapter(ValidationAdapter<?> adapters) {
    super(adapters);
  }

  @Override
  public boolean validate(Object value, ValidationRequest req, String propertyName) {
    return validateArray((Object[]) value, req, propertyName);
  }
}
