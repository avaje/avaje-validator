package example.avaje.pkg_private;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

class PackagePrivateTest {

  Validator validator = Validator.builder().build();

  @Test
  void invalid() {
    var pkgPrivate = new PackagePrivate(-100);
    List<ConstraintViolation> violations = violations(pkgPrivate);

    assertThat(violations).hasSize(1);
  }

  List<ConstraintViolation> violations(Object any) {
    return violations(any, Locale.ENGLISH);
  }

  List<ConstraintViolation> violations(Object any, Locale locale) {
    try {
      validator.validate(any, locale);
      fail("not expected");
      return null;
    } catch (ConstraintViolationException e) {
      return new ArrayList<>(e.violations());
    }
  }
}
