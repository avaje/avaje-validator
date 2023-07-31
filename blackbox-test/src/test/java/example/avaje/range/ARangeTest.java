package example.avaje.range;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ARangeTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    var value = new ARange(1, 3L);
    validator.validate(value);
  }

  @Test
  void min() {
    var violation = one(new ARange(0, 3L));
    assertThat(violation.message()).isEqualTo("must be between 1 and 3");
  }

  @Test
  void minDE() {
    var violation = one(new ARange(0, 3L), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss zwischen 1 und 3 sein");
  }

  @Test
  void max() {
    var violation = one(new ARange(1, 4L));
    assertThat(violation.message()).isEqualTo("must be between 1 and 3");
  }

  @Test
  void maxDE() {
    var violation = one(new ARange(4, 1L), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss zwischen 1 und 3 sein");
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
