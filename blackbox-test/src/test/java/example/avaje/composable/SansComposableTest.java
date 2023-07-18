package example.avaje.composable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Set;

import org.junit.jupiter.api.Test;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

class SansComposableTest {

  final Validator validator = Validator.builder().build();

  @Test
  void valid() {
    final var undertale = new Sans(10);
    validator.validate(undertale);
  }

  @Test
  void invalid() {
    var violations = violations(new Sans(-10));

    violations.forEach(
        v -> assertThat(v.message()).isEqualTo("must have positive double digit amount of puns"));

    violations = violations(new Sans(-420));

    violations.forEach(
        v -> assertThat(v.message()).isEqualTo("must have positive double digit amount of puns"));
  }

  Set<ConstraintViolation> violations(Object any) {

    return validator.validate(any);
  }
}
