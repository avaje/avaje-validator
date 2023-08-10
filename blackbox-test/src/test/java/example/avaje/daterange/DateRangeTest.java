package example.avaje.daterange;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class DateRangeTest {

  Validator validator = Validator.builder()
    .temporalTolerance(Duration.ofMinutes(1))
    .build();

  Instant instantP1 = Instant.now().plus(1, ChronoUnit.DAYS);
  Instant instantP3 = Instant.now().plus(3, ChronoUnit.DAYS);
  Instant instantM1 = Instant.now().minus(1, ChronoUnit.DAYS);
  Instant instantM3 = Instant.now().minus(3, ChronoUnit.DAYS);

  @Test
  void valid() {
    validator.validate(new DateRangeNowTolerance0(LocalDate.now(), Year.now(), YearMonth.now()));
    validator.validate(new DateRangeNowTolerance(Instant.now(), LocalDateTime.now(), OffsetDateTime.now(), ZonedDateTime.now()));
    validator.validate(new DateRangeNow(LocalDate.now(), YearMonth.now().minusYears(4)));
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

  @Test
  void invalidNowDates() {
    var violations = validator.check(new DateRangeNowTolerance0(LocalDate.now().minusDays(1), Year.now().minusYears(1), YearMonth.now().minusYears(1)));
    assertThat(violations).hasSize(3);
  }

  @Test
  void invalidNowDatesAfter() {
    var violations = validator.check(new DateRangeNowTolerance0(LocalDate.now().plusDays(1), Year.now().plusYears(1), YearMonth.now().plusYears(1)));
    assertThat(violations).hasSize(3);
  }

  @Test
  void invalidNowTimestamps() {
    var violations = validator.check(new DateRangeNowTolerance(Instant.now().minusSeconds(90),
      LocalDateTime.now().minusSeconds(90), OffsetDateTime.now().minusSeconds(90), ZonedDateTime.now().minusSeconds(90)));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidNowTimestampsAfter() {
    var violations = validator.check(new DateRangeNowTolerance(Instant.now().plusSeconds(90),
      LocalDateTime.now().plusSeconds(90), OffsetDateTime.now().plusSeconds(90), ZonedDateTime.now().plusSeconds(90)));
    assertThat(violations).hasSize(4);
  }

}
