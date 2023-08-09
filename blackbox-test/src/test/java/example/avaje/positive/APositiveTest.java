package example.avaje.positive;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class APositiveTest {

  Validator validator = Validator.builder().build();

  byte byteOne = 0x1;
  byte byteZero = 0x0;
  byte byteNeg = -0x1;
  short shortOne = 1;
  short shortZero = 0;
  short shortNeg = -1;

  @Test
  void validNull() {
    validator.validate(new APositiveDouble(null, null, null, null));
    validator.validate(new APositiveFloat(null, null, null, null));
  }

  @Test
  void valid() {
    validator.validate(new APositiveDouble(1d, 0d, -1d, 0d));
    validator.validate(new APositiveFloat(1f, 0f, -1f, 0f));
    validator.validate(new APrimitivePositive(byteOne, shortOne, 1, 1, 1d, 1f));
    validator.validate(new APrimitivePositiveOrZero(byteOne, shortOne, 1, 1, 1d, 1f));
    validator.validate(new APrimitivePositiveOrZero(byteZero, shortZero, 0, 0, 0d, 0f));
    validator.validate(new APrimitiveNegativeOrZero(byteZero, shortZero, 0, 0, 0d, 0f));
    validator.validate(new APrimitiveNegative(byteNeg, shortNeg, -1, -1, -1d, -1f));
  }

  @Test
  void invalid() {
    var violations = new ArrayList<>(validator.check(new APositiveDouble(0d, -0.1d, 1d, 0.1d)));
    assertThat(violations).hasSize(4);
    violations = new ArrayList<>(validator.check(new APositiveFloat(0f, -0.1f, 1f, 0.1f)));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidPositiveWhenZero() {
    var violations = new ArrayList<>(validator.check(new APrimitivePositive(byteZero, shortZero, 0, 0, 0d, 0f)));
    assertThat(violations).hasSize(6);
    for (ConstraintViolation violation : violations) {
      assertThat(violation.message()).isEqualTo("must be greater than 0");
    }
  }

  @Test
  void invalidNegativeWhenZero() {
    var violations = new ArrayList<>(validator.check(new APrimitiveNegative(byteZero, shortZero, 0, 0, 0d, 0f)));
    assertThat(violations).hasSize(6);
    for (ConstraintViolation violation : violations) {
      assertThat(violation.message()).isEqualTo("must be less than 0");
    }
  }

  @Test
  void invalidPostiveOrZeroWhenNegative() {
    var violations = new ArrayList<>(validator.check(new APrimitivePositiveOrZero(byteNeg, shortNeg, -1, -1, -1d, -1f)));
    assertThat(violations).hasSize(6);
    for (ConstraintViolation violation : violations) {
      assertThat(violation.message()).isEqualTo("must be greater than or equal to 0");
    }
  }

  @Test
  void invalidNegativeOrZeroWhenPositive() {
    var violations = new ArrayList<>(validator.check(new APrimitiveNegativeOrZero(byteOne, shortOne, 1, 1, 1d, 1f)));
    assertThat(violations).hasSize(6);
    for (ConstraintViolation violation : violations) {
      assertThat(violation.message()).isEqualTo("must be less than or equal to 0");
    }
  }
}
