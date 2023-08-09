package example.avaje.crossfield.adapter;

import example.avaje.crossfield.APassingSkill;
import example.avaje.crossfield.ATarnished;
import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

@ConstraintAdapter(APassingSkill.class)
public final class APassingSkillAdapter extends AbstractConstraintAdapter<ATarnished> {

  public APassingSkillAdapter(AdapterCreateRequest request) {
    super(request);
  }

  @Override
  public boolean isValid(ATarnished lowlyTarnished) {
    if (lowlyTarnished == null) {
      return true;
    }
    return lowlyTarnished.vigor() >= 50 && lowlyTarnished.endurance() >= 50;
  }
}
