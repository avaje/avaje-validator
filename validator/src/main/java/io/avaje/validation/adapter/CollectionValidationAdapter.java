package io.avaje.validation.adapter;

import java.util.Collection;

class CollectionValidationAdapter extends AbstractMultiAdapter {

  public CollectionValidationAdapter(ValidationAdapter<?> adapters) {
    super(adapters);
  }

  @Override
  public boolean validate(Object value, ValidationRequest req, String propertyName) {
    return validateAll((Collection<Object>) value, req, propertyName);
  }
}
