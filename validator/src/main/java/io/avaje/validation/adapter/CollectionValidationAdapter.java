package io.avaje.validation.adapter;

import java.util.Collection;

class CollectionValidationAdapter<T> extends AbstractMultiAdapter<T> {

  public CollectionValidationAdapter(ValidationAdapter<T> adapters) {
    super(adapters);
  }

  @Override
  public boolean validate(Object value, ValidationRequest req, String propertyName) {
    if (validate(value, req, propertyName)) {
      return validateAll((Collection<Object>) value, req, propertyName);
    }

    return true;
  }
}
