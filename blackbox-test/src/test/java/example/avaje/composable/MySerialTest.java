package example.avaje.composable;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class MySerialTest {

  final Validator validator = Validator.builder()
    .addResourceBundles("example.avaje.CustomMessages")
    .addLocales(Locale.GERMAN)
    .build();

  @Test
  void valid() {
    validator.validate(new MySerialExample("A", "Hi"));
    validator.validate(new MySerialExample("ABCDE", "Hi"));
  }

  @Test
  void notValid() {
    var violation = one(new MySerialExample("*", "Hi"));
    assertThat(violation.message()).isEqualTo("Invalid my serial");

    assertThat(one(new MySerialExample("ABCDEF", "Hi")).message()).isEqualTo("Invalid my serial");
    assertThat(one(new MySerialExample("", "Hi")).message()).isEqualTo("Invalid my serial");
    assertThat(one(new MySerialExample("123456789", "Hi")).message()).isEqualTo("Invalid my serial");
  }

  @Test
  void notValid_DE() {
    var violation = one(new MySerialExample("*", "Hi"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("Invalid my serial"); // not translated for DE
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
