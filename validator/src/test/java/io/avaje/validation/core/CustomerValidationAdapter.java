package io.avaje.validation.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
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

  private final ValidationAdapter<Address> billingAddressValidator;
  private final ValidationAdapter<Address> shippingAddressValidator;
  //private final ValidationAdapter<Address> addressValidator;

  public CustomerValidationAdapter(ValidationContext ctx) {
    this.activeAdapter =
        ctx.<Boolean>adapter(AssertTrue.class, Map.of("message", "not true"));

    this.nameAdapter =
        ctx
            .<String>adapter(NotNull.class, Map.of("message","{avaje.NotNull.message}"))
            .andThen(ctx.adapter(NotBlank.class, Map.of("message", "empty")));

    this.activeDateAdapter =
        ctx.adapter(Past.class, Map.of("message", "not in the past", "_type", "Temporal.LocalDate"));

    this.contactsValidator =
        ctx.<List<Contact>>adapter(
                Size.class, Map.of("message", "not sized correctly", "min", 0, "max", 2))
            .list()
            .andThenMulti(ctx.adapter(Contact.class));

    // this.addressValidator = ctx.adapter(Address.class);
    this.shippingAddressValidator = ctx.adapter(Address.class);

    // Option A: billingAddressValidator combines NotNull + addressValidator
    this.billingAddressValidator = ctx.<Address>adapter(NotNull.class, Map.of("message","{avaje.NotNull.message}"))
            .andThen(ctx.adapter(Address.class));

    // Option B: billingAddressValidator only does NotNull ...
    //this.billingAddressValidator = ctx.<Address>adapter(NotNull.class, Collections.emptyMap());
  }

  @Override
  public boolean validate(Customer value, ValidationRequest request, String propertyName) {
    activeAdapter.validate(value.active, request, "active");
    nameAdapter.validate(value.name, request, "name");
    activeDateAdapter.validate(value.activeDate, request, "activeDate");

    // Option A:
    billingAddressValidator.validate(value.billingAddress, request, "billingAddress");

    // Option B: Is more like cascading on collection
    //final var _billingAddress = value.billingAddress;
    //if (billingAddressValidator.validate(_billingAddress, request, "billingAddress")) {
    //  addressValidator.validate(_billingAddress, request, "billingAddress");
    //}

    final var _shippingAddress = value.shippingAddress;
    if (_shippingAddress != null) { // is nullable
      shippingAddressValidator.validate(_shippingAddress, request, "shippingAddress");
    }

    final var _contacts = value.contacts;
    //if field is nullable we could do _contacts != null && contactsValidator.validate
    contactsValidator.validate(_contacts, request, "contacts");
    return true;
  }
}
