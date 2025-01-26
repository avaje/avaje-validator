package example.avaje.repeat;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class SignupRequestTest {

  final Validator validator = Validator.builder()
    .addResourceBundles("example.avaje.CustomMessages")
    .build();

  @Test
  void lowercaseNoSpecial() {
    SignupRequest req = new SignupRequest("foo");

    var violations = all(req, Locale.ENGLISH);
    assertThat(violations).hasSize(3);
    assertThat(violations.get(0).message()).isEqualTo("Signup password size error");
    assertThat(violations.get(1).message()).isEqualTo("Signup must have at least 1 upper case");
    assertThat(violations.get(2).message()).isEqualTo("Signup special character");
  }

  @Test
  void missingDigit() {
    SignupRequest req = new SignupRequest("fooBar!");

    var violations = all(req, Locale.ENGLISH);
    assertThat(violations).hasSize(1);
    assertThat(violations.get(0).message()).isEqualTo("Signup digit");
  }

  List<ConstraintViolation> all(Object any, Locale locale) {
    try {
      validator.validate(any, locale);
      fail("not expected");
      return List.of();
    } catch (ConstraintViolationException e) {
      return new ArrayList<>(e.violations());
    }
  }
}
