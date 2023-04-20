package io.avaje.validation.core;

import java.util.Map;

import io.avaje.validation.AnnotationValidationAdapter;
import io.avaje.validation.ValidationAdapter;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public final class CustomerValidationAdapter implements ValidationAdapter<Customer> {

  private final AnnotationValidationAdapter<Boolean> activeAdapter;
  private final AnnotationValidationAdapter<String> nameAdapter;
  private final AnnotationValidationAdapter<Object> activeDateAdapter;

  public CustomerValidationAdapter(Validator validator) {
    this.activeAdapter =
        validator.<Boolean>annotationAdapter(AssertTrue.class).init(Map.of("message", "not true"));

    this.nameAdapter =
        validator
            .<String>annotationAdapter(NotNull.class)
            .init(Map.of("message", "null"))
            .andThen(
                validator.<String>annotationAdapter(NotBlank.class).init(Map.of("message", "empty")));

    this.activeDateAdapter =
        validator.annotationAdapter(Past.class).init(Map.of("message", "not in the past"));
  }

  @Override
  public void validate(Customer value, ValidationRequest request) {
    activeAdapter.validate(value.active, request);
    nameAdapter.validate(value.name, request);
    activeDateAdapter.validate(value.activeDate, request);
  }
}
