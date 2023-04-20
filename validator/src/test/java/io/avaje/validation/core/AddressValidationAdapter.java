package io.avaje.validation.core;

import io.avaje.validation.Validator;
import io.avaje.validation.adapter.AnnotationValidationAdapter;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.util.Map;

public final class AddressValidationAdapter implements ValidationAdapter<Address> {

  private final AnnotationValidationAdapter<String> line1Adapter;
//  private final AnnotationValidationAdapter<String> line2Adapter;
//  private final AnnotationValidationAdapter<Long> longValueAdapter;

  public AddressValidationAdapter(Validator validator) {
    this.line1Adapter =
        validator
            .<String>annotationAdapter(NotNull.class)
            .init(Map.of("message", "null"))
            .andThen(
                validator.<String>annotationAdapter(NotBlank.class).init(Map.of("message", "empty")));

  }

  @Override
  public boolean validate(Address pojo, ValidationRequest request, String propertyName) {
    if (propertyName != null) {
      request.pushPath(propertyName);
    }
    line1Adapter.validate(pojo.line1, request, "line1");
    //line2Adapter.validate(pojo.line2, request);
    //longValueAdapter.validate(pojo.longValue, request);
    if (propertyName != null) {
      request.popPath();
    }
    return true;
  }
}
