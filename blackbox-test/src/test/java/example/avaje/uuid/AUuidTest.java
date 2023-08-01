package example.avaje.uuid;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class AUuidTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    var value = new AUuid(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    validator.validate(value);
  }

  @Test
  void validNull() {
    var value = new AUuid(null, null);
    validator.validate(value);
  }

  @Test
  void asString() {
    var violation = one(new AUuid("Not", UUID.randomUUID().toString()));
    assertThat(violation.message()).isEqualTo("must be a valid UUID");
  }

  @Test
  void asStringDE() {
    var violation = one(new AUuid("Not", UUID.randomUUID().toString()), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss eine gültige UUID sein");
  }

  @Test
  void asCharSequence() {
    var violation = one(new AUuid(UUID.randomUUID().toString(), "Not"));
    assertThat(violation.message()).isEqualTo("must be a valid UUID");
  }

  @Test
  void asCharSequenceDE() {
    var violation = one(new AUuid(UUID.randomUUID().toString(), "Not"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss eine gültige UUID sein");
  }

  ConstraintViolation one(Object any) {
    return one(any, Locale.ENGLISH);
  }

  ConstraintViolation one(Object any, Locale locale) {
    try {
      validator.validate(any, locale);
      fail("not expected");
      return null;
    } catch (ConstraintViolationException e) {
      var violations = new ArrayList<>(e.violations());
      assertThat(violations).hasSize(1);
      return violations.get(0);
    }
  }
}
