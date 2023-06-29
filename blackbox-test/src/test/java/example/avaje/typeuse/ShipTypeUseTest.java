package example.avaje.typeuse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import example.avaje.ACustomer;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

class ShipTypeUseTest {

  final Validator validator = Validator.builder().failFast(true).build();

  @Test
  void valid() {
    final var cust = new Ship(Map.of("juice", new CrewMate("medbay")), List.of("medbay"));
    validator.validate(cust);
  }

  @Test
  void blank() {
    var violation = one(new Ship(Map.of("", new CrewMate("")), null));
    assertThat(violation.message()).isEqualTo("Names cannot be blank");
    violation = one(new Ship(Map.of("tank", new CrewMate("")), null));
    assertThat(violation.message()).isEqualTo("Must have valid task");
    violation = one(new Ship(Map.of("tank", new CrewMate("gh")), List.of("")));
    assertThat(violation.message()).isEqualTo("Tasks cannot be blank");
  }

  ConstraintViolation one(Object any) {
    try {
      validator.validate(any);
      fail("not expected");
      return null;
    } catch (final ConstraintViolationException e) {
      final var violations = new ArrayList<>(e.violations());
      assertThat(violations).hasSize(1);
      return violations.get(0);
    }
  }
}
