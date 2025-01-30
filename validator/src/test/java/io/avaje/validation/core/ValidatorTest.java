package io.avaje.validation.core;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ValidatorTest {

  private final Validator validator =
      Validator.builder()
              .add(Customer.class, CustomerValidationAdapter::new)
              .add(Address.class, AddressValidationAdapter::new)
              .add(Contact.class, ContactValidationAdapter::new)
              .build();

  @Test
  void cacheForType() {
    ValidationContext ctx = (ValidationContext) validator;

    ValidationAdapter<Customer> adapter0 = ctx.adapter(Customer.class);
    ValidationAdapter<Customer> adapter1 = ctx.adapter(Customer.class);
    assertThat(adapter0).isSameAs(adapter1);
  }

  @Test
  void testAllFail() {
    try {
      validator.validate(new Customer(false, null, LocalDate.now().plusDays(3)));
      fail("");
    } catch (ConstraintViolationException e) {
      Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(3);
    }
  }

  @Test
  void testStrDate() {
    try {
      validator.validate(new Customer(true, "  ", LocalDate.now().plusDays(3)));
    } catch (ConstraintViolationException e) {
      Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(2);
    }
  }

  @Test
  void testStrOnly() {
    try {
    validator.validate(new Customer(true, "  ", LocalDate.now().minusDays(3)));
    } catch (ConstraintViolationException e) {
      Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(1);
    }
  }

  @Test
  void testRecurse() {
    try {
      var cust = new Customer(false, null, LocalDate.now().plusDays(3));
      cust.billingAddress.line1 = null;

      var c0 = new Contact();
      var c1 = new Contact(null, "hi");
      c1.address = new Address();
      cust.contacts = List.of(c0, c1 , c0, c0);
      validator.validate(cust);
      fail("");
    } catch (ConstraintViolationException e) {
      Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(6);
      List<ConstraintViolation> asList = new ArrayList<>(violations);

      var last = asList.get(violations.size() - 1);
      assertThat(last.path()).isEqualTo("contacts[1].address.line1");
      assertThat(last.field()).isEqualTo("line1");
      assertThat(last.message()).isEqualTo("myCustomNullMessage");
    }
  }

  @Test
  void contactWhenLastNameNull() {
    try {
      var contact = new Contact("first", null);
      validator.validate(contact);
      fail("");
    } catch (ConstraintViolationException e) {
      Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(1);
      List<ConstraintViolation> asList = new ArrayList<>(violations);

      var first = asList.get(0);
      assertThat(first.path()).isEqualTo("lastName");
      assertThat(first.field()).isEqualTo("lastName");
      assertThat(first.message()).isEqualTo("must not be null");
    }
  }

  @Test
  void customerWhenBillingAddressNull() {
    try {
      var customer = new Customer(true, " success ", LocalDate.now().minusDays(3));
      customer.billingAddress = null;
      validator.validate(customer);

      fail("");
    } catch (ConstraintViolationException e) {
      Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(1);
      List<ConstraintViolation> asList = new ArrayList<>(violations);

      var first = asList.get(0);
      assertThat(first.path()).isEqualTo("billingAddress");
      assertThat(first.field()).isEqualTo("billingAddress");
      assertThat(first.message()).isEqualTo("must not be null");
    }
  }

  @Test
  void testSuccess() {
    validator.validate(new Customer(true, " success ", LocalDate.now().minusDays(3)));
  }
}
