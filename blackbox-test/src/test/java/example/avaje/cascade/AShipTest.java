package example.avaje.cascade;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class AShipTest {

  Validator validator = Validator.builder().build();

  @Test
  void valid() {
    var ship = new AShip("lollyPop", List.of(new ACrew("ok", null)));
    validator.validate(ship);
  }

  @Test
  void validAllowEmptyCollection() {
    var ship = new AShip("lollyPop", List.of());
    validator.validate(ship);
  }

  @Test
  void validAllowNullCollection() {
    var ship = new AShip("lollyPop", null);
    validator.validate(ship);
  }

  @Test
  void valid2() {
    var ship = new AShip2("lollyPop", List.of(new ACrew("ok", null)));
    validator.validate(ship);
  }

  @Test
  void valid3_expect_noCascadeValidationToCrew() {
    var ship = new AShip3("lollyPop", List.of(new ACrew("NotValid", null)));
    validator.validate(ship);
  }

  @Test
  void invalid() {
    var ship = new AShip("", List.of(new ACrew("NotValid", null)));
    List<ConstraintViolation> violations = violations(ship);

    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).path()).isEqualTo("name");
    assertThat(violations.get(0).message()).isEqualTo("must not be blank");
    assertThat(violations.get(1).path()).isEqualTo("crew[0].name");
    assertThat(violations.get(1).message()).isEqualTo("maximum length 4 exceeded");
  }

  @Test
  void invalidShip2_expect_InvalidEmptyCollection() {
    var ship = new AShip2("", List.of());
    List<ConstraintViolation> violations = violations(ship);

    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).path()).isEqualTo("name");
    assertThat(violations.get(0).message()).isEqualTo("must not be blank");
    assertThat(violations.get(1).path()).isEqualTo("crew");
    assertThat(violations.get(1).message()).isEqualTo("must not be empty");
  }

  @Test
  void invalidShip2_when_nullCollection_expect_InvalidEmptyCollection() {
    var ship = new AShip2("", null);
    List<ConstraintViolation> violations = violations(ship);

    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).path()).isEqualTo("name");
    assertThat(violations.get(0).message()).isEqualTo("must not be blank");
    assertThat(violations.get(1).path()).isEqualTo("crew");
    assertThat(violations.get(1).message()).isEqualTo("must not be empty");
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
