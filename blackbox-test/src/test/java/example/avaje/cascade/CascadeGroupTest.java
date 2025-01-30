package example.avaje.cascade;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import example.avaje.cascade.CascadeGroup.Cascaded;
import io.avaje.validation.Validator;

class CascadeGroupTest {

  Validator validator =
      Validator.builder()
          .add(CascadeGroup.class, CascadeGroupValidationAdapter::new)
          .add(CascadeGroup.Cascaded.class, CascadeGroup$CascadedValidationAdapter::new)
          .build();

  @Test
  void valid() {
    var value = new CascadeGroup(new Cascaded(""));
    assertThat(validator.check(value)).isEmpty();
  }

  @Test
  void validGroup() {
    var value = new CascadeGroup(new Cascaded(""));
    assertThat(validator.check(value, CascadeGroup.class).iterator().next())
        .matches(c -> "must not be blank".equals(c.message()));
  }
}
