package io.avaje.validation.core;

import io.avaje.validation.adapter.AdapterBuildContext;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public final class AddressValidationAdapter implements ValidationAdapter<Address> {

  private final ValidationAdapter<String> line1Adapter;
//  private final AnnotationValidationAdapter<String> line2Adapter;
//  private final AnnotationValidationAdapter<Long> longValueAdapter;

  public AddressValidationAdapter(AdapterBuildContext validator) {
    this.line1Adapter =
        validator
            .<String>adapter(NotNull.class, Map.of("message", "null"))
            .andThen(
                validator.adapter(NotBlank.class, Map.of("message", "empty")));
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
