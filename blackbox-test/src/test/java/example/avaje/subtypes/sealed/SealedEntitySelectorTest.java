package example.avaje.subtypes.sealed;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class SealedEntitySelectorTest {

  Validator validator = Validator.builder().build();

  @Test
  void validByIdSelector() {
    var entity = new SealedEntity(new ByIdSelectorSealed(List.of()));

    assertThat(validator.check(entity).iterator().next().message()).isEqualTo("must not be empty");
  }

  @Test
  void validByIdQuery() {
    var entity = new SealedEntity(new ByQuerySelectorSealed(""));
    assertThat(validator.check(entity).iterator().next().message()).isEqualTo("must not be blank");
  }
}
