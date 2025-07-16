package example.avaje.cascade;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class AShipTest {

  Validator validator = Validator.instance();

  @Test
  void valid() {
    var ship = new AShip("lollyPop", List.of(new ACrew("ok")));
    validator.validate(ship);
  }

  @Test
  void valid_usingSet() {
    var ship = new BShip("lollyPop", Set.of(new ACrew("ok")));
    validator.validate(ship);
  }

  @Test
  void valid_usingArray() {
    var ship = new CShip("lollyPop", new ACrew[]{new ACrew("ok")});
    validator.validate(ship);
  }

  @Test
  void valid_usingArray3() {
    var ship = new CShip3("lollyPop", new ACrew[]{new ACrew("ok")});
    validator.validate(ship);
  }

  @Test
  void valid_usingScalarArray() {
    var ship = new DShip("lollyPop", "ok", new String[]{"bob"});
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
    var ship = new AShip2("lollyPop", List.of(new ACrew("ok")));
    validator.validate(ship);
  }

  @Test
  void valid3_expect_noCascadeValidationToCrew() {
    var ship = new AShip3("lollyPop", List.of(new ACrew("NotValid")));
    validator.validate(ship);
  }

  @Test
  void invalid() {
    var ship = new AShip("", List.of(new ACrew("NotValid")));
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

  @Test
  void arrayCascade() {
    var ship = new CShip("", new ACrew[]{new ACrew("NotValid")});
    List<ConstraintViolation> violations = violations(ship);

    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).path()).isEqualTo("name");
    assertThat(violations.get(0).message()).isEqualTo("must not be blank");
    assertThat(violations.get(1).path()).isEqualTo("crew[0].name");
    assertThat(violations.get(1).message()).isEqualTo("maximum length 4 exceeded");
  }

  @Test
  void arrayNotCascade() {
    var ship = new CShip3("", new ACrew[]{new ACrew("NotValid")});
    List<ConstraintViolation> violations = violations(ship);

    assertThat(violations).hasSize(1);
    assertThat(violations.get(0).path()).isEqualTo("name");
    assertThat(violations.get(0).message()).isEqualTo("must not be blank");
  }

  @Test
  void arrayNotEmpty_when_empty() {
    var ship = new CShip2("", new ACrew[]{});
    List<ConstraintViolation> violations = violations(ship);

    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).path()).isEqualTo("name");
    assertThat(violations.get(0).message()).isEqualTo("must not be blank");
    assertThat(violations.get(1).path()).isEqualTo("crew");
    assertThat(violations.get(1).message()).isEqualTo("must not be empty");
  }

  @Test
  void arrayNotEmpty_when_null() {
    var ship = new CShip2("ok", null);
    List<ConstraintViolation> violations = violations(ship);

    assertThat(violations).hasSize(1);
    assertThat(violations.get(0).path()).isEqualTo("crew");
    assertThat(violations.get(0).message()).isEqualTo("must not be empty");
  }

  @Test
  void arrayNotEmpty_when_scalarArrayEmpty() {
    var ship = new DShip("", "ok", new String[]{});
    List<ConstraintViolation> violations = violations(ship);

    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).path()).isEqualTo("name");
    assertThat(violations.get(0).message()).isEqualTo("must not be blank");
    assertThat(violations.get(1).path()).isEqualTo("crew");
    assertThat(violations.get(1).message()).isEqualTo("must not be empty");
  }

  @Test
  void arrayNotEmpty_when_scalarNull() {
    var ship = new DShip("ok", "ok", null);
    List<ConstraintViolation> violations = violations(ship);

    assertThat(violations).hasSize(1);
    assertThat(violations.get(0).path()).isEqualTo("crew");
    assertThat(violations.get(0).message()).isEqualTo("must not be empty");
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
