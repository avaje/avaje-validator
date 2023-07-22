package io.avaje.validation.generator.models.valid;

import java.util.Map;
import java.util.Set;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.generator.models.valid.CheckCase.CaseMode;

@ConstraintAdapter(CheckCase.class)
public final class CustomAnnotationAdapter extends AbstractConstraintAdapter<String> {

  private final CaseMode caseMode;

  public CustomAnnotationAdapter(
      ValidationContext ctx, Set<Class<?>> groups, Map<String, Object> attributes) {
    super(ctx.message(attributes), groups);
    caseMode = (CaseMode) attributes.get("caseMode");
  }

  @Override
  public boolean isValid(String object) {

    if (object == null) {

      return true;
    }

    return caseMode != CaseMode.UPPER
        || !object.equals(object.toUpperCase()) && !object.equals(object.toLowerCase());
  }
}
