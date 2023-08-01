package example.avaje.uri;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class AUriTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  private static final String validUri = "http://foo:81";

  @Test
  void valid() {
    var value = new AUri("http://localhost/banana", validUri, "https://foo");
    validator.validate(value);
  }

  @Test
  void validNull() {
    var value = new AUri(null, null, null);
    validator.validate(value);
  }

  @Test
  void notScheme() {
    var violation = one(new AUri("Not", validUri, null));
    assertThat(violation.message()).isEqualTo("must be a valid URI");
  }

  @Test
  void notHost() {
    var violation = one(new AUri("http://foo", validUri, null));
    assertThat(violation.message()).isEqualTo("must be a valid URI");
  }

  @Test
  void notPort() {
    var violation = one(new AUri(null, "http://foo:78", null));
    assertThat(violation.message()).isEqualTo("must be a valid URI");
  }

  @Test
  void notRegex() {
    var violation = one(new AUri(null, null, "ftp://foo"));
    assertThat(violation.message()).isEqualTo("must be a valid URI");
  }

  @Test
  void asStringDE() {
    var violation = one(new AUri("Not", validUri, null), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss eine gültige URI sein");
  }

  @Test
  void asCharSequence() {
    var violation = one(new AUri(null, "Not", null));
    assertThat(violation.message()).isEqualTo("must be a valid URI");
  }

  @Test
  void asCharSequenceDE() {
    var violation = one(new AUri(null, "Not", null), Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss eine gültige URI sein");
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
