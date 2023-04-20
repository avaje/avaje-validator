package io.avaje.validation.core;

import java.util.Map;
import java.util.Set;

import io.avaje.validation.AnnotationValidationAdapter;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ValidationAdapter;
import io.avaje.validation.Validator;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public final class AuthProvider$PojoAdapter implements ValidationAdapter<Pojo> {

  private final AnnotationValidationAdapter<Boolean> booleanAdapter;
  private final AnnotationValidationAdapter<String> strAdapter;
  private final AnnotationValidationAdapter<Object> dateAdapter;

  public AuthProvider$PojoAdapter(Validator jsonb) {
    this.booleanAdapter =
        jsonb.<Boolean>annotationAdapter(AssertTrue.class).init(Map.of("message", "not true"));

    this.strAdapter =
        jsonb
            .<String>annotationAdapter(NotNull.class)
            .init(Map.of("message", "null"))
            .andThen(
                jsonb.<String>annotationAdapter(NotBlank.class).init(Map.of("message", "empty")));

    this.dateAdapter =
        jsonb.annotationAdapter(Past.class).init(Map.of("message", "not in the past"));
  }

  @Override
  public void validate(Pojo value, Set<ConstraintViolation> violations) {
    booleanAdapter.validate(value.bool, violations);
    strAdapter.validate(value.str, violations);
    dateAdapter.validate(value.date, violations);
  }
}
