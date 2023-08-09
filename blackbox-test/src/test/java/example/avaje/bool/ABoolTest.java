package example.avaje.bool;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ABoolTest {

  Validator validator = Validator.builder().build();

  @Test
  void valid() {
    validator.validate(new ABoolTrue(true, null));
    validator.validate(new ABoolTrue(true, true));
    validator.validate(new ABoolFalse(false, null));
    validator.validate(new ABoolFalse(false, false));
  }

  @Test
  void invalidTrue() {
    var violations = new ArrayList<>(validator.check(new ABoolTrue(false, false)));
    violations.stream()
      .map(ConstraintViolation::message)
      .forEach(msg -> assertThat(msg).isEqualTo("must be true"));
  }

  @Test
  void invalidFalse() {
    var violations = new ArrayList<>(validator.check(new ABoolFalse(true, true)));
    violations.stream()
      .map(ConstraintViolation::message)
      .forEach(msg -> assertThat(msg).isEqualTo("must be false"));
  }
}
