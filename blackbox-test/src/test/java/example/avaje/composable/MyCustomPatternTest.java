package example.avaje.composable;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class MyCustomPatternTest {

  final Validator validator = Validator.builder()
    .addResourceBundles("example.avaje.CustomMessages")
    .addLocales(Locale.GERMAN)
    .build();

  @Test
  void valid() {
    validator.validate(new MyCustomPattern("SDFS", "Hi"));
    validator.validate(new MyCustomPattern("12", "Hi"));
    validator.validate(new MyCustomPattern("12345678", "Hi"));
    validator.validate(new MyCustomPattern("A234567Z", "Hi"));
    validator.validate(new MyCustomPattern("A2_456_Z", "Hi"));
  }

  @Test
  void notValid() {
    var violation = one(new MyCustomPattern("SD*FS", "Hi"));
    assertThat(violation.message()).isEqualTo("Invalid MyKey");

    assertThat(one(new MyCustomPattern("S", "Hi")).message()).isEqualTo("Invalid MyKey");
    assertThat(one(new MyCustomPattern("", "Hi")).message()).isEqualTo("Invalid MyKey");
    assertThat(one(new MyCustomPattern("123456789", "Hi")).message()).isEqualTo("Invalid MyKey");
  }

  @Test
  void notValid_DE() {
    var violation = one(new MyCustomPattern("SD*FS", "Hi"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("darf nicht MyKey");
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
