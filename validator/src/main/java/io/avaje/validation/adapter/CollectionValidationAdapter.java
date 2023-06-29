package io.avaje.validation.adapter;

import java.util.Collection;

class CollectionValidationAdapter<T> extends AbstractMultiAdapter<T> {

   CollectionValidationAdapter(ValidationAdapter<T> adapters) {
    super(adapters);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean validate(T value, ValidationRequest req, String propertyName) {
    if (starterAdapter.validate(value, req, propertyName)) {
      return validateAll((Collection<Object>) value, req, propertyName);
    }

    return true;
  }
}
