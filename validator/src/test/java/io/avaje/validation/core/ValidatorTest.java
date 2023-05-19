package io.avaje.validation.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

class ValidatorTest {

  private final Validator validator =
      Validator.builder()
              .add(Customer.class, CustomerValidationAdapter::new)
              .add(Address.class, AddressValidationAdapter::new)
              .add(Contact.class, ContactValidationAdapter::new)
              .build();

  @Test
  void testAllFail() {
    try {
      validator.validate(new Customer(false, null, LocalDate.now().plusDays(3)));
      fail("");
    } catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(3);
    }
  }

  @Test
  void testStrDate() {
    try {
      validator.validate(new Customer(true, "  ", LocalDate.now().plusDays(3)));
    } catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(2);
    }
  }

  @Test
  void testStrOnly() {
    try {
    validator.validate(new Customer(true, "  ", LocalDate.now().minusDays(3)));
    } catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(1);
    }
  }

  @Test
  void testRecurse() {
    try {
      final var cust = new Customer(false, null, LocalDate.now().plusDays(3));
      cust.billingAddress.line1 = null;

      final var c0 = new Contact();
      final var c1 = new Contact(null, "hi");
      c1.address = new Address();
      cust.contacts = List.of(c0, c1 , c0, c0);
      validator.validate(cust);
      fail("");
    } catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(7);
      final List<ConstraintViolation> asList = new ArrayList<>(violations);

      final var last = asList.get(violations.size() - 1);
      assertThat(last.path()).isEqualTo("contacts.1.address");
      assertThat(last.propertyName()).isEqualTo("line1");
      assertThat(last.message()).isEqualTo("myCustomNullMessage");
    }
  }

  @Test
  void contactWhenLastNameNull() {
    try {
      final var contact = new Contact("first", null);
      validator.validate(contact);
      fail("");
    } catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(1);
      final List<ConstraintViolation> asList = new ArrayList<>(violations);

      final var first = asList.get(0);
      assertThat(first.path()).isEqualTo("");
      assertThat(first.propertyName()).isEqualTo("lastName");
      assertThat(first.message()).isEqualTo("must not be null");
    }
  }

  @Test
  void customerWhenBillingAddressNull() {
    try {
      final var customer = new Customer(true, " success ", LocalDate.now().minusDays(3));
      customer.billingAddress = null;
      validator.validate(customer);

      fail("");
    } catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation> violations = e.violations();
      assertThat(violations).hasSize(1);
      final List<ConstraintViolation> asList = new ArrayList<>(violations);

      final var first = asList.get(0);
      assertThat(first.path()).isEqualTo("");
      assertThat(first.propertyName()).isEqualTo("billingAddress");
      assertThat(first.message()).isEqualTo("must not be null");
    }
  }

  @Test
  void testSuccess() {
    validator.validate(new Customer(true, " success ", LocalDate.now().minusDays(3)));
  }
}
