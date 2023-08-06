package example.avaje.positive;

import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class APositiveTest {

  Validator validator = Validator.builder().build();

  @Test
  void validNull() {
    validator.validate(new APositiveDouble(null, null, null, null));
    validator.validate(new APositiveFloat(null, null, null, null));

  }

  @Test
  void valid() {
    validator.validate(new APositiveDouble(1d, 0d, -1d, 0d));
    validator.validate(new APositiveFloat(1f, 0f, -1f, 0f));
  }

  @Test
  void invalid() {
    var violations = new ArrayList<>(validator.check(new APositiveDouble(0d, -0.1d, 1d, 0.1d)));
    assertThat(violations).hasSize(4);
    violations = new ArrayList<>(validator.check(new APositiveFloat(0f, -0.1f, 1f, 0.1f)));
    assertThat(violations).hasSize(4);
  }
}
