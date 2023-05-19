package io.avaje.validation.core;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class SizeLocaleTest extends BasicTest {

  @Test
  void testSize_DefaultLocale() {
    var contact = new Contact("ok", "IAmTooLong");
    ConstraintViolation constraint = one(contact, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("size must be between 0 and 5");
  }

  @Test
  void testSize_DE() {
    var contact = new Contact("ok", "IAmTooLong");
    ConstraintViolation constraint = one(contact, Locale.GERMAN);
    assertThat(constraint.message()).isEqualTo("Größe muss zwischen 0 und 5 sein");
  }

}
