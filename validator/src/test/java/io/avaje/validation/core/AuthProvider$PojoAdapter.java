package io.avaje.validation.core;

import java.util.Map;
import java.util.Set;

import io.avaje.validation.ValidationAdapter;
import io.avaje.validation.Validator;
import io.avaje.validation.ConstraintViolation;
import jakarta.validation.constraints.AssertTrue;

public final class AuthProvider$PojoAdapter implements ValidationAdapter<Pojo> {

  private final AnnotationValidationAdapter<Boolean> booleanAdapter;

  public AuthProvider$PojoAdapter(Validator jsonb) {
    this.booleanAdapter =
        jsonb.<Boolean>annotationAdapter(AssertTrue.class).init(Map.of("message", "not true"));
  }

  @Override
  public void validate(Pojo value, Set<ConstraintViolation> violations) {

    violations.add(booleanAdapter.validate(value.bool));
  }
}
