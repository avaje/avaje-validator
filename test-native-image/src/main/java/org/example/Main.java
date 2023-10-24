package org.example;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
      Validator validator = Validator.builder()
        .addLocales(Locale.GERMAN)
        .build();

      var customer = new Customer("hello", "");
      System.out.println("violations EN - " + validator.check(customer));
      System.out.println("violations DE - " + validator.check(customer, Locale.GERMAN));

      if (!"must not be blank".equals(first(validator.check(customer)).message())) {
        System.exit(1);
      }
      if (!"darf nicht leer sein".equals(first(validator.check(customer, Locale.GERMAN)).message())) {
        System.exit(1);
      }
    }

  private static ConstraintViolation first(Set<ConstraintViolation> check) {
    return new ArrayList<>(check).get(0);
  }
}
