package example.jakarta;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class JMyPatternTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    final var bean = new JMyPattern("12");
    validator.validate(bean);
  }

  @Test
  void pattern() {
    final var violation = one(new JMyPattern("abc"));
    assertThat(violation.message()).isEqualTo("must match \"[0-3]+\"");
  }

  @Test
  void patternDE() {
    final var violation = one(new JMyPattern("abc"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss mit \"[0-3]+\" übereinstimmen");
  }

  ConstraintViolation one(Object any) {
    return one(any, Locale.ENGLISH);
  }

  ConstraintViolation one(Object any, Locale locale) {

    final var violations = new ArrayList<>(validator.validate(any, locale));

    if (violations.isEmpty()) throw new IllegalStateException();

    assertThat(violations).hasSize(1);
    return violations.get(0);
  }
}
