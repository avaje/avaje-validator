package example.jakarta;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class JMyEmailTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    var bean = new JMyEmail("rob@foo.com");
    validator.validate(bean);
  }

  @Test
  void email() {
    var violation = one(new JMyEmail("abc"));
    assertThat(violation.message()).isEqualTo("must be a well-formed email address");
  }

  @Test
  void emailDE() {
    var violation = one(new JMyEmail("abc"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss eine korrekt formatierte E-Mail-Adresse sein");
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
