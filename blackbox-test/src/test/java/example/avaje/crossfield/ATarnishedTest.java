package example.avaje.crossfield;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ATarnishedTest {

  Validator validator = Validator.builder().build();

  @Test
  void valid() {
    validator.validate(new ATarnished("ok", 50, 50));
  }

  @Test
  void invalid_classLevelValidation() {
    var violation = one(new ATarnished("ok", 49, 50));
    assertThat(violation.message()).isEqualTo("put these foolish ambitions to rest");
    assertThat(violation.path()).isEqualTo("");
    assertThat(violation.field()).isEqualTo("");
  }


  @Test
  void invalidField_expect_classValidationToNotRun() {
    var violation = one(new ATarnished(" ", 49, 50));
    assertThat(violation.message()).isEqualTo("must not be blank");
    assertThat(violation.path()).isEqualTo("name");
    assertThat(violation.field()).isEqualTo("name");
  }

  @Test
  void invalidField2_expect_classValidationToNotRun() {
    var violation = one(new ATarnished("ok", -1, 50));
    assertThat(violation.message()).isEqualTo("must be greater than 0");
    assertThat(violation.path()).isEqualTo("vigor");
    assertThat(violation.field()).isEqualTo("vigor");
  }

  ConstraintViolation one(Object any) {
    try {
      validator.validate(any);
      fail("not expected");
      return null;
    } catch (ConstraintViolationException e) {
      var violations = new ArrayList<>(e.violations());
      assertThat(violations).hasSize(1);
      return violations.get(0);
    }
  }
}
