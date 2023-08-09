package example.avaje.range;

import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class APrimitiveTest {

  Validator validator = Validator.builder().build();

  @Test
  void rangeLongValid() {
    validator.validate(new APrimitiveLongRange(1));
    validator.validate(new APrimitiveLongRange(2));
    validator.validate(new APrimitiveLongRange(3));
  }

  @Test
  void rangeLongBelowMin() {
    var violations  = new ArrayList<>(validator.check(new APrimitiveLongRange(0)));
    assertThat(violations).hasSize(1);
    assertThat(violations.get(0).message()).isEqualTo("must be between 1 and 3");
  }

  @Test
  void rangeLongAboveMax() {
    var violations  = new ArrayList<>(validator.check(new APrimitiveLongRange(4)));
    assertThat(violations).hasSize(1);
    assertThat(violations.get(0).message()).isEqualTo("must be between 1 and 3");
  }
}
