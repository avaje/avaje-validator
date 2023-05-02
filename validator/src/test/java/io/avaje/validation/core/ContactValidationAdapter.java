package io.avaje.validation.core;

import java.util.Map;

import io.avaje.validation.adapter.AdapterBuildContext;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class ContactValidationAdapter implements ValidationAdapter<Contact> {

  private final ValidationAdapter<String> firstNameAdapter;
  private final ValidationAdapter<String> lastNameAdapter;
  private final ValidationAdapter<Address> addressValidator;

  public ContactValidationAdapter(AdapterBuildContext validator) {
    this.firstNameAdapter =
        validator
            .<String>adapter(NotNull.class, Map.of("message", "null"))
            .andThen(validator.adapter(NotBlank.class, Map.of("message", "empty")));

    this.lastNameAdapter =
        validator.<String>adapter(NotNull.class, Map.of("message", "null"));
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
