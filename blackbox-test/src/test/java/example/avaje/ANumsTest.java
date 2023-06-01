package example.avaje;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ANumsTest {

  final Validator validator = Validator.builder().addLocals(Locale.GERMAN).build();

  @Test
  void valid() {
    validator.validate(new ANums());
  }

  @Test
  void digits() {
    var bean = new ANums();
    bean.digits = "12345678";
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("numeric value out of bounds (<5 digits>.<3 digits> expected)");
  }

  @Test
  void digitsDE() {
    var bean = new ANums();
    bean.digits = "12345678";
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("numerischer Wert außerhalb des gültigen Bereichs (<5 digits>.<3 digits> erwartet)");
  }

  @Test
  void digitsDecimal() {
    var bean = new ANums();
    bean.digitsDecimal = new BigDecimal("12345678");
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("numeric value out of bounds (<4 digits>.<2 digits> expected)");
  }

  @Test
  void digitsDecimalDE() {
    var bean = new ANums();
    bean.digitsDecimal = new BigDecimal("12345678");
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("numerischer Wert außerhalb des gültigen Bereichs (<4 digits>.<2 digits> erwartet)");
  }

  @Test
  void positive() {
    var bean = new ANums();
    bean.positive = -1;
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be greater than 0");
  }

  @Test
  void positiveDE() {
    var bean = new ANums();
    bean.positive = -1;
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss größer als 0 sein");
  }

  @Test
  void positiveOrZero() {
    var bean = new ANums();
    bean.positiveOrZero = -1;
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be greater than or equal to 0");
  }

  @Test
  void positiveOrZeroDE() {
    var bean = new ANums();
    bean.positiveOrZero = -1;
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss größer-gleich 0 sein");
  }

  @Test
  void negative() {
    var bean = new ANums();
    bean.negative = 1;
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be less than 0");
  }

  @Test
  void negativeDE() {
    var bean = new ANums();
    bean.negative = 1;
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner als 0 sein");
  }

  @Test
  void negativeOrZero() {
    var bean = new ANums();
    bean.negativeOrZero = 1;
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be less than or equal to 0");
  }

  @Test
  void negativeOrZeroDE() {
    var bean = new ANums();
    bean.negativeOrZero = 1;
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner-gleich 0 sein");
  }

  ConstraintViolation one(Object any) {
    return one(any, Locale.ENGLISH);
  }

  ConstraintViolation one(Object any, Locale locale) {
    try {
      validator.validate(any, locale);
      fail("not expected");
      return null;
    } catch (ConstraintViolationException e) {
      var violations = new ArrayList<>(e.violations());
      assertThat(violations).hasSize(1);
      return violations.get(0);
    }
  }
}
