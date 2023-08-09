package example.avaje.custom;

import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ACustomLongTest {

  Validator validator = Validator.builder().addResourceBundles("example.avaje.CustomMessages").build();

  @Test
  void valid() {
    validator.validate(new ACustomLong(4, 3L));
    validator.validate(new ACustomLong(4, null));
  }

  @Test
  void invalid() {
    var violations = new ArrayList<>(validator.check(new ACustomLong(3, 4L)));
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).message()).isEqualTo("Invalid special number");
    assertThat(violations.get(1).message()).isEqualTo("Invalid special number");
  }
}
