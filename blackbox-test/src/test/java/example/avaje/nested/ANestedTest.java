package example.avaje.nested;

import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ANestedTest {

  final Validator validator = Validator.builder().build();

  @Test
  void validateNested() {
    final var contact = new AContact("Rob", "TooLargeForHere");
    contact.address = new AAddress("", "TooBigHere", -1);

    final var violations = new ArrayList<>(validator.validate(contact));

    assertThat(violations).hasSize(4);

    final var v0 = violations.get(0);
    assertThat(v0.path()).isEmpty();
    assertThat(v0.propertyName()).isEqualTo("lastName");
    assertThat(v0.message()).isEqualTo("size must be between 0 and 5");

    final var v1 = violations.get(1);
    assertThat(v1.path()).isEqualTo("address");
    assertThat(v1.propertyName()).isEqualTo("line1");
    assertThat(v1.message()).isEqualTo("must not be blank");

    final var v2 = violations.get(2);
    assertThat(v2.path()).isEqualTo("address");
    assertThat(v2.propertyName()).isEqualTo("line2");
    assertThat(v2.message()).isEqualTo("size must be between 0 and 4");

    final var v3 = violations.get(3);
    assertThat(v3.path()).isEqualTo("address");
    assertThat(v3.propertyName()).isEqualTo("longValue");
    assertThat(v3.message()).isEqualTo("must be greater than 0");
  }
}
