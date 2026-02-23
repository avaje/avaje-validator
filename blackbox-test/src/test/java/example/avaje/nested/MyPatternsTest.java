package example.avaje.nested;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class MyPatternsTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void patternWithEscapes_valid() {
    var bean = new MyPatterns("34.23");
    validator.validate(bean);
  }

  @Test
  void patternWithEscapes_invalid_tooFew() {
    var bean = new MyPatterns("34.2");
    var violation = one(bean, Locale.ENGLISH);
    assertThat(violation.message()).isEqualTo("Not a valid amount. please add 2 decimal behind");
  }

  @Test
  void patternWithEscapes_invalid_tooMany() {
    var bean = new MyPatterns("34.234");
    var violation = one(bean, Locale.ENGLISH);
    assertThat(violation.message()).isEqualTo("Not a valid amount. please add 2 decimal behind");
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
