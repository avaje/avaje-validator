package example.avaje.length;

import example.avaje.ACustomer;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ALengthTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    var cust = new ALength("ok", "ok", "ok", "ok");
    validator.validate(cust);
  }

  @Test
  void blank() {
    var violation = one(new ALength("", "ok", "ok", "ok"));
    assertThat(violation.message()).isEqualTo("length must be between 1 and 3");
  }

  @Test
  void blankDE() {
    var violation = one(new ALength("", "ok", "ok", "ok"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("Länge muss zwischen 1 und 3 sein");
  }

  @Test
  void lengthMax() {
    var violation = one(new ALength("TooLarge", "ok", "ok", "ok"));
    assertThat(violation.message()).isEqualTo("length must be between 1 and 3");
  }

  @Test
  void lengthOnlyMax() {
    var violation = one(new ALength("ok", "TooLargeHere", "ok", "ok"));
    assertThat(violation.message()).isEqualTo("maximum length 5 exceeded");
  }

  @Test
  void lengthMaxDE() {
    var violation = one(new ALength("TooLarge", "ok", "ok", "ok"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("Länge muss zwischen 1 und 3 sein");
  }

  @Test
  void lengthMinMax() {
    var violation = one(new ACustomer("valid", "Other", "TooLarge"));
    assertThat(violation.message()).isEqualTo("size must be between 2 and 4");
  }

  @Test
  void lengthCustomMessage() {
    var violation = one(new ALength("ok", "ok", "ok", ""));
    assertThat(violation.message()).isEqualTo("Custom length message");
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
