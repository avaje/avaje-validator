package io.avaje.validation.generator.models.valid.typeconstraint;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;
import io.avaje.validation.generator.models.valid.Captain;

@ConstraintAdapter(FraudWatch.class)
public final class FraudAdapter extends AbstractConstraintAdapter<Captain> {

  public FraudAdapter(AdapterCreateRequest request) {
    super(request);
  }

  @Override
  public boolean isValid(Captain captain) {

    return !"ukitake".equals(captain.name()) || "Jakuhō Raikōben".equals(captain.bankai().name());
  }
}
