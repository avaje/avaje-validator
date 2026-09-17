package example.avaje.mapcascade;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import io.avaje.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class MapCascadeTest {

  private final Validator validator = Validator.builder().build();

  @Test
  void validMapValue_hasNoViolations() {
    var violations = validator.check(new MapCascade(Map.of("default", new MapTemplate("valid"))));

    assertThat(violations).isEmpty();
  }

  @Test
  void invalidMapValue_isValidatedWithKeyPath() {
    var violations = validator.check(new MapCascade(Map.of("default", new MapTemplate(""))));

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().path()).isEqualTo("getDynamicBlocks[default].getName");
  }

  @Test
  void multipleInvalidMapValues_areValidatedWithKeyPaths() {
    var violations = validator.check(new MapCascade(Map.of("first", new MapTemplate(""), "second", new MapTemplate(""))));

    assertThat(violations)
      .extracting(ConstraintViolation::path)
      .containsExactlyInAnyOrder("getDynamicBlocks[first].getName", "getDynamicBlocks[second].getName");
  }

  @Test
  void nullMapValue_isSkipped() {
    var values = new HashMap<String, MapTemplate>();
    values.put("null", null);

    assertThat(validator.check(new MapCascade(values))).isEmpty();
  }

  @Test
  void nullMap_isRejectedByNotNull() {
    var violations = validator.check(new MapCascade(null));

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().path()).isEqualTo("getDynamicBlocks");
  }

  @Test
  void emptyMap_isRejectedBySize() {
    var violations = validator.check(new MapCascade(Map.of()));

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().path()).isEqualTo("getDynamicBlocks");
  }
}
