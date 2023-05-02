package io.avaje.validation.core;

import java.util.List;
import java.util.Map;

import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public final class CustomerValidationAdapter implements ValidationAdapter<Customer> {

  private final ValidationAdapter<Boolean> activeAdapter;
  private final ValidationAdapter<String> nameAdapter;
  private final ValidationAdapter<Object> activeDateAdapter;
  private final ValidationAdapter<List<Contact>> contactsValidator;

  private final ValidationAdapter<Address> addressValidator;
  private final ValidationAdapter<Contact> contactValidator;

  public CustomerValidationAdapter(Validator validator) {
    this.activeAdapter =
        validator.<Boolean>adapter(AssertTrue.class, Map.of("message", "not true"));

    this.nameAdapter =
        validator
            .<String>adapter(NotNull.class, Map.of("message", "null"))
            .andThen(validator.adapter(NotBlank.class, Map.of("message", "empty")));

    this.activeDateAdapter =
        validator.adapter(Past.class, Map.of("message", "not in the past"));

    this.contactsValidator =
        validator.<List<Contact>>adapter(
            Size.class, Map.of("message", "not sized correctly", "min", 0, "max", 2));

    addressValidator = validator.adapter(Address.class);
    contactValidator = validator.adapter(Contact.class);
  }

  @Override
  public boolean validate(Customer value, ValidationRequest request, String propertyName) {
    activeAdapter.validate(value.active, request, "active");
    nameAdapter.validate(value.name, request, "name");
    activeDateAdapter.validate(value.activeDate, request, "activeDate");

    final var _billingAddress = value.billingAddress;
      addressValidator.validate(_billingAddress, request, "billingAddress");

    final var _shippingAddress = value.shippingAddress;
    if (_shippingAddress != null) { // is nullable
      addressValidator.validate(_shippingAddress, request, "shippingAddress");
    }

    final var _contacts = value.contacts;
    //if field is nullable we could do _contacts != null && contactsValidator.validate
    if (contactsValidator.validate(_contacts, request, "contacts")) {
      contactValidator.validateAll(_contacts, request, "contacts");
    }
    return true;
  }
}
