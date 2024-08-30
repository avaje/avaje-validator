package example.avaje.jspecify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

public class JSpecifyTest {

  final Validator validator =
      Validator.builder()
          .add(JSpecifyNotNull.class, JSpecifyNotNullValidationAdapter::new)
          .add(JSpecifyNullUnmarked.class, JSpecifyNullUnmarkedValidationAdapter::new)
          .addLocales(Locale.GERMAN)
          .build();

  @Test
  void valid() {
    var value = new JSpecifyNotNull("ok", "ok", "ok");
    validator.validate(value);
    validator.validate(new JSpecifyNullUnmarked(null));
  }

  @Test
  void inValidNull() {
    var value = new JSpecifyNotNull(null, null, null);
    try {
      validator.validate(value);
    } catch (ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(2);
    }
  }
}
