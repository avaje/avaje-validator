package io.avaje.validation.generator.models.valid;

import io.avaje.validation.adapter.ConstraintAdapter;
import io.avaje.validation.adapter.PrimitiveAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

@ConstraintAdapter(PrimitiveTest.class)
public final class PrimitiveTestAdapter extends PrimitiveAdapter<Long> {

  public PrimitiveTestAdapter(AdapterCreateRequest request) {
    super(request);
  }

  @Override
  public boolean isValid(Long object) {
    return false;
  }
}
