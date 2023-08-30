package io.avaje.validation.core.adapters;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

import java.util.UUID;

final class UuidAdapter extends AbstractConstraintAdapter<Object> {

  UuidAdapter(AdapterCreateRequest request) {
    super(request);
  }

  @Override
  protected boolean isValid(Object value) {
    if (value == null) {
      return true;
    }
    try {
      UUID.fromString(String.valueOf(value));
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
