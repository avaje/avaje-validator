package io.avaje.validation.core;

import io.avaje.validation.Validator;

public final class PojoValidationAdapter {//implements ValidationAdapter<Pojo> {

  //private final AnnotationValidationAdapter<Boolean> booleanAdapter;
//  private final ValidationAdapter<Pojo.Address> addressValidation = null;
//  private final ValidationAdapter<Pojo.Contact> contactValidation = null;
//
//  private final ScalarValidator<String> firstNameValidator = null;
//
//  private final CoreValidation coreValidation = null;

  public PojoValidationAdapter(Validator jsonb) {
    //this.booleanAdapter =
    //    jsonb.<Boolean>annotationAdapter(AssertTrue.class).init(Map.of("message", "not true"));
  }

//  @Override
//  public void validate(Pojo value, ValidationRequest ctx) {
//
//    if (firstNameValidator.required(ctx, value.firstName, 2, 300)) {
//      firstNameValidator.min(ctx, value.firstName, "a");
//    }
//
//    if (coreValidation.required(ctx, value.billingAddress)) {
//      // recurse
//      ctx.pushPath("billingAddress");
//      addressValidation.validate(value.billingAddress, ctx);
//      ctx.popPath();
//    }
//
//    final var _shippingAddress = value.shippingAddress;
//    if (_shippingAddress != null) { // optional
//      // recurse
//      ctx.pushPath("shippingAddress");
//      addressValidation.validate(value.shippingAddress, ctx);
//      ctx.popPath();
//    }
//
//    final var _contacts = value.contacts;
//    // collection empty & size validations
//    if (coreValidation.collection(ctx, _contacts, 0, 100)) {
//      ctx.pushPath("contacts");
//      // recurse
//      contactValidation.validateAll(value.contacts, ctx);
//      ctx.popPath();
//    }
//
//  }
//
//  @Override
//  public void validateAll(Collection<Pojo> value, ValidationRequest ctx) {
//    int position = 0;
//    for (Pojo pojo : value) {
//      ctx.pushPath(String.valueOf(position));
//      position++;
//      validate(pojo, ctx);
//      ctx.popPath();
//    }
//  }
}
