package io.avaje.validation.adapter;

import java.util.Collection;

abstract class AbstractMultiAdapter implements ValidationAdapter<Object> {

  private final ValidationAdapter<Object> adapters;

  protected AbstractMultiAdapter(ValidationAdapter<?> adapters) {
    this.adapters = (ValidationAdapter<Object>) adapters;
  }

  protected boolean validateAll(
      Collection<Object> value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = -1;
    for (final var element : value) {
      index++;
      adapters.validate(element, req, String.valueOf(index));
    }
    if (propertyName != null) {
      req.popPath();
    }
    return true;
  }

  protected boolean validateArray(Object[] value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = -1;
    for (final Object element : value) {
      index++;
      adapters.validate(element, req, String.valueOf(index));
    }
    if (propertyName != null) {
      req.popPath();
    }
    return true;
  }
}
