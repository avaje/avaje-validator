package example.jakarta;

import example.avaje.ANums;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class JNumsTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    validator.validate(new JNums());
  }

  @Test
  void digits() {
    final var bean = new JNums();
    bean.digits = "12345678";
    final var violation = one(bean);
    assertThat(violation.message()).isEqualTo("numeric value out of bounds (<5 digits>.<3 digits> expected)");
  }

  @Test
  void digitsDE() {
    final var bean = new JNums();
    bean.digits = "12345678";
    final var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("numerischer Wert außerhalb des gültigen Bereichs (<5 digits>.<3 digits> erwartet)");
  }

  @Test
  void digitsDecimal() {
    final var bean = new ANums();
    bean.digitsDecimal = new BigDecimal("12345678");
    final var violation = one(bean);
    assertThat(violation.message()).isEqualTo("numeric value out of bounds (<4 digits>.<2 digits> expected)");
  }

  @Test
  void digitsDecimalDE() {
    final var bean = new ANums();
    bean.digitsDecimal = new BigDecimal("12345678");
    final var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("numerischer Wert außerhalb des gültigen Bereichs (<4 digits>.<2 digits> erwartet)");
  }

  @Test
  void positive() {
    final var bean = new JNums();
    bean.positive = -1;
    final var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be greater than 0");
  }

  @Test
  void positiveDE() {
    final var bean = new JNums();
    bean.positive = -1;
    final var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss größer als 0 sein");
  }

  @Test
  void positiveOrZero() {
    final var bean = new JNums();
    bean.positiveOrZero = -1;
    final var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be greater than or equal to 0");
  }

  @Test
  void positiveOrZeroDE() {
    final var bean = new JNums();
    bean.positiveOrZero = -1;
    final var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss größer-gleich 0 sein");
  }

  @Test
  void negative() {
    final var bean = new JNums();
    bean.negative = 1;
    final var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be less than 0");
  }

  @Test
  void negativeDE() {
    final var bean = new JNums();
    bean.negative = 1;
    final var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner als 0 sein");
  }

  @Test
  void negativeOrZero() {
    final var bean = new JNums();
    bean.negativeOrZero = 1;
    final var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be less than or equal to 0");
  }

  @Test
  void negativeOrZeroDE() {
    final var bean = new JNums();
    bean.negativeOrZero = 1;
    final var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner-gleich 0 sein");
  }

  ConstraintViolation one(Object any) {
    return one(any, Locale.ENGLISH);
  }

  ConstraintViolation one(Object any, Locale locale) {

    final var violations = new ArrayList<>(validator.validate(any, locale));

    if (violations.isEmpty()) throw new IllegalStateException();

    assertThat(violations).hasSize(1);
    return violations.get(0);
  }
}
