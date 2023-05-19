package io.avaje.validation.core;

import io.avaje.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class NotNullLocaleTest extends BasicTest {

  @Test
  void testSize_DefaultLocale() {
    var contact = new Contact("ok", null);
    ConstraintViolation constraint = one(contact, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("must not be null");
  }

  @Test
  void testSize_DE() {
    var contact = new Contact("ok", null);
    ConstraintViolation constraint = one(contact, Locale.GERMAN);
    assertThat(constraint.message()).isEqualTo("darf nicht null sein");
  }

}
