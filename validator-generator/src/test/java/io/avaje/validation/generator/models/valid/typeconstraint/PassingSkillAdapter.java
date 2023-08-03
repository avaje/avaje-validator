package io.avaje.validation.generator.models.valid.typeconstraint;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

@ConstraintAdapter(PassingSkill.class)
public final class PassingSkillAdapter extends AbstractConstraintAdapter<Tarnished> {

  public PassingSkillAdapter(AdapterCreateRequest request) {
    super(request);
  }

  @Override
  public boolean isValid(Tarnished lowlyTarnished) {
    if (lowlyTarnished == null) {
      return true;
    }
    return lowlyTarnished.vigor() >= 50 && lowlyTarnished.endurance() >= 50;
  }
}
