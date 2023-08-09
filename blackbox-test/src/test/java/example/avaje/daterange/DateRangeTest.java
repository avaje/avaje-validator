package example.avaje.daterange;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class DateRangeTest {

  Validator validator = Validator.builder().build();

  Instant instantP1 = Instant.now().plus(1, ChronoUnit.DAYS);
  Instant instantP3 = Instant.now().plus(3, ChronoUnit.DAYS);
  Instant instantM1 = Instant.now().minus(1, ChronoUnit.DAYS);
  Instant instantM3 = Instant.now().minus(3, ChronoUnit.DAYS);

  @Test
  void valid() {
    validator.validate(new DateRangeLocal(LocalDate.now(), Instant.now()));
    validator.validate(new DateRangeLocal(LocalDate.now().minusDays(1), instantM1));
    validator.validate(new DateRangeLocal(LocalDate.now().plusDays(1), instantP1));
  }

  @Test
  void invalidAboveRange() {
    var violations = new ArrayList<>(validator.check(new DateRangeLocal(LocalDate.now().plusDays(3), instantP3)));
    assertThat(violations).hasSize(2);
    violations.stream()
      .map(ConstraintViolation::message)
      .forEach(msg -> assertThat(msg).isEqualTo("must be in the date range"));
  }

  @Test
  void invalidBelowRange() {
    var violations = new ArrayList<>(validator.check(new DateRangeLocal(LocalDate.now().minusDays(3), instantM3)));
    assertThat(violations).hasSize(2);
    violations.stream()
      .map(ConstraintViolation::message)
      .forEach(msg -> assertThat(msg).isEqualTo("must be in the date range"));
  }
}
