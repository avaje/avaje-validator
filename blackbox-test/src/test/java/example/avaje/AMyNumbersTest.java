package example.avaje;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class AMyNumbersTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    var bean = new AMyNumbers(ONE, ONE);
    validator.validate(bean);
  }

  @Test
  void validDouble() {
    var bean = new AMyNumbers(1d, 1d);
    validator.validate(bean);
  }

  @Test
  void decimalMax() {
    var violation = one(new AMyNumbers(new BigDecimal("11"), ONE));
    assertThat(violation.message()).isEqualTo("must be less than or equal to 10.50");
  }

  @Test
  void decimalMaxDE() {
    var violation = one(new AMyNumbers(new BigDecimal("11"), ONE), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner oder gleich 10.50 sein");
  }

  @Test
  void decimalMaxExclusive() {
    var violation = one(new AMyNumbers(ONE, new BigDecimal("11")));
    assertThat(violation.message()).isEqualTo("must be less than 9.30");
  }

  @Test
  void decimalMaxExclusiveDE() {
    var violation = one(new AMyNumbers(ONE, new BigDecimal("11")), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner 9.30 sein");
  }

  @Test
  void doubleDecimalMax() {
    var violation = one(new AMyNumbers(11d, 1d));
    assertThat(violation.message()).isEqualTo("must be less than or equal to 9.50");
  }

  @Test
  void doubleDecimalMaxDE() {
    var violation = one(new AMyNumbers(11d, 1d), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner oder gleich 9.50 sein");
  }

  @Test
  void doubleDecimalMaxExclusive() {
    var violation = one(new AMyNumbers(1d, 11d));
    assertThat(violation.message()).isEqualTo("must be less than 8.30");
  }

  @Test
  void doubleDecimalMaxExclusiveDE() {
    var violation = one(new AMyNumbers(1d, 11d), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner 8.30 sein");
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
