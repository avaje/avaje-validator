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

class JMyMinNumbersTest {

  final Validator validator = Validator.builder().build();

  final BigDecimal valid = new BigDecimal("20");

  @Test
  void valid() {
    var bean = new JMyMinNumbers(valid, valid);
    validator.validate(bean);
  }

  @Test
  void decimalMin() {
    var violation = one(new JMyMinNumbers(ONE, valid));
    assertThat(violation.message()).isEqualTo("must be greater than or equal to 10.50");
  }

  @Test
  void decimalMinDE() {
    var violation = one(new JMyMinNumbers(ONE, valid), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss größer oder gleich 10.50 sein");
  }

  @Test
  void decimalMinExclusive() {
    var violation = one(new JMyMinNumbers(valid, ONE));
    assertThat(violation.message()).isEqualTo("must be greater than 9.30");
  }

  @Test
  void decimalMaxExclusiveDE() {
    var violation = one(new JMyMinNumbers(valid, ONE), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss größer 9.30 sein");
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
