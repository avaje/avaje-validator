package io.avaje.validation.core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

import static org.assertj.core.api.Assertions.*;

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
      assertThat(violations).hasSize(7);
      List<ConstraintViolation> asList = new ArrayList<>(violations);

      var last = asList.get(violations.size() - 1);
      assertThat(last.path()).isEqualTo("contacts.1.address");
      assertThat(last.propertyName()).isEqualTo("line1");
      assertThat(last.message()).isEqualTo("empty");
    }
  }

  @Test
  void testSuccess() {
    validator.validate(new Customer(true, " success ", LocalDate.now().minusDays(3)));
  }
}
