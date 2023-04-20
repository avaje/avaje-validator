package io.avaje.validation.core;

import io.avaje.validation.Validator;
import io.avaje.validation.adapter.AnnotationValidationAdapter;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public final class ContactValidationAdapter implements ValidationAdapter<Contact> {

  private final AnnotationValidationAdapter<String> firstNameAdapter;
  private final AnnotationValidationAdapter<String> lastNameAdapter;
  private final ValidationAdapter<Address> addressValidator;


  public ContactValidationAdapter(Validator validator) {
    this.firstNameAdapter =
        validator
            .<String>annotationAdapter(NotNull.class)
            .init(Map.of("message", "null"))
            .andThen(
                validator.<String>annotationAdapter(NotBlank.class).init(Map.of("message", "empty")));

    this.lastNameAdapter =  validator
            .<String>annotationAdapter(NotNull.class);
    this.addressValidator = validator.adapter(Address.class);
  }

  @Override
  public boolean validate(Contact pojo, ValidationRequest request, String propertyName) {
    if (propertyName != null) {
      request.pushPath(propertyName);
    }
    firstNameAdapter.validate(pojo.firstName, request, "firstName");
    lastNameAdapter.validate(pojo.lastName, request, "lastName");

    final var _address = pojo.address;
    if (_address != null) { // optional / Nullable
      addressValidator.validate(_address, request, "address");
    }

    if (propertyName != null) {
      request.popPath();
    }
    return true;
  }
}
