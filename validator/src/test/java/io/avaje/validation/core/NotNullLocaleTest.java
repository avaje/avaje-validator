package io.avaje.validation.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import io.avaje.validation.ConstraintViolation;

class NotNullLocaleTest extends BasicTest {

  @Test
  void testSize_DefaultLocale() {
    final var contact = new Contact("ok", null);
    final ConstraintViolation constraint = one(contact, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("must not be null");
  }

  @Test
  void testSize_groups() {
    final var contact = new Contact("ok", null);
    assertThatThrownBy(()->one(contact, Locale.ENGLISH,BasicTest.class)).isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void testSize_DefaultLocale2() {
    final var contact = new Contact("ok", null);
    final ConstraintViolation constraint = one(contact, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("must not be null");
  }

  @Test
  void testSize_DE() {
    final var contact = new Contact("ok", null);
    final ConstraintViolation constraint = one(contact, Locale.GERMAN);
    assertThat(constraint.message()).isEqualTo("darf nicht null sein");
  }

}
