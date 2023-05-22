package example.jakarta;

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

class JMyNumbersTest {

  final Validator validator = Validator.builder().build();

  @Test
  void valid() {
    var bean = new JMyNumbers(ONE, ONE, "12345.123");
    validator.validate(bean);
  }

  @Test
  void decimalMax() {
    var violation = one(new JMyNumbers(new BigDecimal("11"), ONE, "12345.123"));
    assertThat(violation.message()).isEqualTo("must be less than or equal to 10.50");
  }

  @Test
  void decimalMaxDE() {
    var violation = one(new JMyNumbers(new BigDecimal("11"), ONE, "12345.123"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner oder gleich 10.50 sein");
  }

  @Test
  void decimalMaxExclusive() {
    var violation = one(new JMyNumbers(ONE, new BigDecimal("11"), "12345.123"));
    assertThat(violation.message()).isEqualTo("must be less than 9.30");
  }

  @Test
  void decimalMaxExclusiveDE() {
    var violation = one(new JMyNumbers(ONE, new BigDecimal("11"), "12345.123"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss kleiner 9.30 sein");
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
