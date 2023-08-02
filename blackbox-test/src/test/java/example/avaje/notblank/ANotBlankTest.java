package example.avaje.notblank;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ANotBlankTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    var value = new ANotBlank("ok", "ok", "ok");
    validator.validate(value);
  }

  @Test
  void inValidNull() {
    var value = new ANotBlank(null, null, null);
    try {
      validator.validate(value);
      fail("not get here");
    } catch (ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(3);
    }
  }

  @Test
  void invalidBlank() {
    var violation = one(new ANotBlank("", "ok", "ok"));
    assertThat(violation.message()).isEqualTo("must not be blank");

    var violation1 = one(new ANotBlank("ok", " ", "ok"));
    assertThat(violation1.message()).isEqualTo("must not be blank");

    var violation2 = one(new ANotBlank("ok", "ok", "\t"));
    assertThat(violation2.message()).isEqualTo("NotBlank n max 4");
  }

  @Test
  void invalidMax() {
    var violation = one(new ANotBlank("ok", "NotValid", "ok"));
    assertThat(violation.message()).isEqualTo("maximum length 4 exceeded");

    var violation1 = one(new ANotBlank("ok", "ok", "NotValid"));
    assertThat(violation1.message()).isEqualTo("NotBlank n max 4");
  }

  @Test
  void invalidAsDE() {
    var violation = one(new ANotBlank(" ", "ok", "ok"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("darf nicht leer sein");
  }

  @Test
  void invalidWithMaxDE() {
    var violation = one(new ANotBlank("ok", "NotValid", "ok"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("Länge muss zwischen 1 und 4 sein");
  }

  @Test
  void invalidCustomMessageDE() {
    var violation = one(new ANotBlank("ok", "ok", "NotValid"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("NotBlank n max 4");
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
