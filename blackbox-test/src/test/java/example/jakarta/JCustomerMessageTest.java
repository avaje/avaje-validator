package example.jakarta;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class JCustomerMessageTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    var cust = new JCustomer("Rob", "Other");
    validator.validate(cust);
  }

  @Test
  void blank() {
    var violation = one(new JCustomer("", "Other"));
    assertThat(violation.message()).isEqualTo("must not be blank");
  }

  @Test
  void blankDE() {
    var violation = one(new JCustomer("", "Other"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("darf nicht leer sein");
  }

  @Test
  void sizeMax() {
    var violation = one(new JCustomer("NameIsTooLarge", "Other"));
    assertThat(violation.message()).isEqualTo("size must be between 0 and 5");
  }

  @Test
  void sizeMaxDE() {
    var violation = one(new JCustomer("NameIsTooLarge", "Other"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("Größe muss zwischen 0 und 5 sein");
  }

  @Test
  void sizeMinMax() {
    var violation = one(new JCustomer("valid", "Other", "TooLarge"));
    assertThat(violation.message()).isEqualTo("size must be between 2 and 4");
  }

  @Test
  void sizeMinMaxDE() {
    var violation = one(new JCustomer("valid", "Other", "TooLarge"), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("Größe muss zwischen 2 und 4 sein");
  }

  @Test
  void sizeMaxCustomMessage() {
    var violation = one(new JCustomer("Valid", "OtherTooLargeForThis"));
    assertThat(violation.message()).isEqualTo("My custom error message with max 7");
  }

  @Test
  void sizeMaxCustomMessageDE() {
    var violation = one(new JCustomer("Valid", "OtherTooLargeForThis"));
    assertThat(violation.message()).isEqualTo("My custom error message with max 7");
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
