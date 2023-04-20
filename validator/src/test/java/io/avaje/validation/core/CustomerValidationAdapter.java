package io.avaje.validation.core;

import java.util.List;
import java.util.Map;

import io.avaje.validation.adapter.CoreValidation;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public final class CustomerValidationAdapter implements ValidationAdapter<Customer> {

  private final ValidationAdapter<Boolean> activeAdapter;
  private final ValidationAdapter<String> nameAdapter;
  private final ValidationAdapter<Object> activeDateAdapter;

  private final CoreValidation core;

  private final ValidationAdapter<Address> addressValidator;
  private final ValidationAdapter<Contact> contactValidator;

  public CustomerValidationAdapter(Validator validator) {
    this.core = validator.core();
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

    addressValidator = validator.adapter(Address.class);
    contactValidator = validator.adapter(Contact.class);
  }

  @Override
  public boolean validate(Customer value, ValidationRequest request, String propertyName) {
    activeAdapter.validate(value.active, request, "active");
    nameAdapter.validate(value.name, request, "name");
    activeDateAdapter.validate(value.activeDate, request, "activeDate");

    final var _billingAddress = value.billingAddress;
    if (core.required(_billingAddress, request, "billingAddress")) { // required / NotNull
      addressValidator.validate(_billingAddress, request, "billingAddress");
    }

    final var _shippingAddress = value.shippingAddress;
    if (_shippingAddress != null) { // is nullable
      addressValidator.validate(_shippingAddress, request, "shippingAddress");
    }

    final var _contacts = value.contacts;
    if (core.collection(request, "contacts", _contacts, 0, 2)) {
      contactValidator.validateAll(_contacts, request, "contacts");
    }
    return true;
  }
}
