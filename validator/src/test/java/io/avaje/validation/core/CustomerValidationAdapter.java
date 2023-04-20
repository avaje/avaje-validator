package io.avaje.validation.core;

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
  }

  @Override
  public boolean validate(Customer value, ValidationRequest request) {
    activeAdapter.validate(value.active, request);
    nameAdapter.validate(value.name, request);
    activeDateAdapter.validate(value.activeDate, request);

    final var _billingAddress = value.billingAddress;
    if (core.required(request, _billingAddress)) { // required / NotNull
      addressValidator.validate(_billingAddress, request);
    }

    final var _shippingAddress = value.shippingAddress;
    if (_shippingAddress != null) { // is nullable
      addressValidator.validate(_shippingAddress, request);
    }
    return true;
  }
}
