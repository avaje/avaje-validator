package io.avaje.validation.core;

import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.Map;

public final class ContactValidationAdapter implements ValidationAdapter<Contact> {

  private final ValidationAdapter<String> firstNameAdapter;
  private final ValidationAdapter<String> lastNameAdapter;
  private final ValidationAdapter<Address> addressValidator;

  public ContactValidationAdapter(ValidationContext ctx) {
    this.firstNameAdapter = ctx.adapter(NotBlank.class, Map.of("message", "empty"));
    this.lastNameAdapter = ctx.<String>adapter(NotNull.class, Map.of("message","{avaje.NotNull.message}"))
      .andThen(ctx.adapter(Size.class, Map.of("max", 5, "min", 0)));
    this.addressValidator = ctx.adapter(Address.class);
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
