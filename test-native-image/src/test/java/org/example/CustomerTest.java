package org.example;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

class CustomerTest {

  @Test
  void test() {
    Validator validator = Validator.builder()
      .addLocales(Locale.GERMAN)
      .build();

    var customer = new Customer("hello", "");
    System.out.println("violations EN - " + validator.check(customer));
    System.out.println("violations DE - " + validator.check(customer, Locale.GERMAN));
  }
}
