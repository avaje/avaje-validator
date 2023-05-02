package io.avaje.validation.core;

import io.avaje.validation.adapter.AdapterContext;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.Map;

public final class ContactValidationAdapter implements ValidationAdapter<Contact> {

  private final ValidationAdapter<String> firstNameAdapter;
  private final ValidationAdapter<String> lastNameAdapter;
  private final ValidationAdapter<Address> addressValidator;

  public ContactValidationAdapter(AdapterContext ctx) {
    this.firstNameAdapter =
        ctx
            .<String>adapter(NotNull.class, Map.of("message", "null"))
            .andThen(ctx.adapter(NotBlank.class, Map.of("message", "empty")));

    this.lastNameAdapter = ctx.adapter(NotNull.class, Collections.emptyMap());
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
