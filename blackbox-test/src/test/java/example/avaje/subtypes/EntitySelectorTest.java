package example.avaje.subtypes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class EntitySelectorTest {

  Validator validator = Validator.builder().build();

  @Test
  void validByIdSelector() {
    var entity = new SubtypeEntity(new ByIdSelector(List.of()));

    assertThat(validator.check(entity).iterator().next().message()).isEqualTo("must not be empty");
  }

  @Test
  void validByIdQuery() {
    var entity = new SubtypeEntity(new ByQuerySelector(""));
    assertThat(validator.check(entity).iterator().next().message()).isEqualTo("must not be blank");
  }
}
