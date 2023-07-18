package example.avaje.nested;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class ANestedWithNullableTest {

  final Validator validator = Validator.builder().build();

  @Test
  void validateNestedSkipNullAddress() {
    final var contact = new AContactWithNullable("Rob", "TooLargeForHere");
    contact.address = null;

    final var violations = new ArrayList<>(validator.validate(contact));

    assertThat(violations).hasSize(1);

    final var v0 = violations.get(0);
    assertThat(v0.path()).isEmpty();
    assertThat(v0.propertyName()).isEqualTo("lastName");
    assertThat(v0.message()).isEqualTo("size must be between 0 and 5");
  }

  @Test
  void validateNestedAddress() {
    final var contact = new AContactWithNullable("Rob", "TooLargeForHere");
    contact.address = new AAddress("line1", "ok", 12);

    final var violations = new ArrayList<>(validator.validate(contact));

    assertThat(violations).hasSize(1);

    final var v0 = violations.get(0);
    assertThat(v0.path()).isEmpty();
    assertThat(v0.propertyName()).isEqualTo("lastName");
    assertThat(v0.message()).isEqualTo("size must be between 0 and 5");
  }

  @Test
  void validateNested() {
    final var contact = new AContactWithNullable("Rob", "TooLargeForHere");
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
