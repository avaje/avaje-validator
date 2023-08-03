package io.avaje.validation.generator.models.valid;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;
import io.avaje.validation.generator.models.valid.CheckCase.CaseMode;

@ConstraintAdapter(CheckCase.class)
public final class CheckCaseAdapter extends AbstractConstraintAdapter<String> {

  private final CaseMode caseMode;

  public CheckCaseAdapter(AdapterCreateRequest request) {
    super(request);
    final var attributes = request.attributes();
    caseMode = (CaseMode) attributes.get("caseMode");
  }

  @Override
  public boolean isValid(String object) {
    if (object == null) {
      return true;
    }
    if (caseMode == CaseMode.UPPER) {
      return object.equals(object.toUpperCase());
    } else {
      return object.equals(object.toLowerCase());
    }
  }
}
