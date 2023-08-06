package example.avaje.past;

import example.jakarta.JPastFuture;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class APastFutureTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    validator.validate(new JPastFuture());
    validator.validate(new APastFutureInstant());
    validator.validate(new APastFutureOffsetTime());
    validator.validate(new APastFutureODT());
    validator.validate(new APastFutureZDT());
    validator.validate(new APastFutureLocalDate());
    validator.validate(new APastFutureLocalDateTime());
    validator.validate(new APastFutureLocalTime());
    validator.validate(new APastFutureDate());
    validator.validate(new APastFutureYearMonth());
    validator.validate(new APastFutureYear());
  }

  @Test
  void future() {
    var bean = new JPastFuture();
    bean.future = LocalDate.now().minusDays(1);
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be a future date");
  }

  @Test
  void futureDE() {
    var bean = new JPastFuture();
    bean.future = LocalDate.now().minusDays(1);
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss ein Datum in der Zukunft sein");
  }

  @Test
  void futureOrPresent() {
    var bean = new JPastFuture();
    bean.futureOrPresent = LocalDate.now().minusDays(1);
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be a date in the present or in the future");
  }

  @Test
  void futureOrPresentDE() {
    var bean = new JPastFuture();
    bean.futureOrPresent = LocalDate.now().minusDays(1);
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss ein Datum in der Gegenwart oder in der Zukunft sein");
  }


  @Test
  void past() {
    var bean = new JPastFuture();
    bean.past = LocalDate.now().plusDays(1);
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be a past date");
  }

  @Test
  void pastDE() {
    var bean = new JPastFuture();
    bean.past = LocalDate.now().plusDays(1);
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss ein Datum in der Vergangenheit sein");
  }


  @Test
  void pastOrPresent() {
    var bean = new JPastFuture();
    bean.pastOrPresent = LocalDate.now().plusDays(1);
    var violation = one(bean);
    assertThat(violation.message()).isEqualTo("must be a date in the past or in the present");
  }

  @Test
  void pastOrPresentDE() {
    var bean = new JPastFuture();
    bean.pastOrPresent = LocalDate.now().plusDays(1);
    var violation = one(bean, Locale.GERMAN);
    assertThat(violation.message()).isEqualTo("muss ein Datum in der Vergangenheit oder in der Gegenwart sein");
  }

  @Test
  void invalidInstant() {
    var violations = new ArrayList<>(validator.check(new APastFutureInstant().makeInvalid()));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidOffsetDateTime() {
    var violations = new ArrayList<>(validator.check(new APastFutureODT().makeInvalid()));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidOffsetTime() {
    var violations = new ArrayList<>(validator.check(new APastFutureOffsetTime().makeInvalid()));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidZonedDateTime() {
    var violations = new ArrayList<>(validator.check(new APastFutureZDT().makeInvalid()));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidLocalDate() {
    var violations = new ArrayList<>(validator.check(new APastFutureLocalDate().makeInvalid()));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidLocalDateTime() {
    var violations = new ArrayList<>(validator.check(new APastFutureLocalDateTime().makeInvalid()));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidLocalTime() {
    var violations = new ArrayList<>(validator.check(new APastFutureLocalDateTime().makeInvalid()));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidDate() {
    var violations = new ArrayList<>(validator.check(new APastFutureDate().makeInvalid()));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidYear() {
    var violations = new ArrayList<>(validator.check(new APastFutureYear().makeInvalid()));
    assertThat(violations).hasSize(4);
  }

  @Test
  void invalidYearMonth() {
    var violations = new ArrayList<>(validator.check(new APastFutureYearMonth().makeInvalid()));
    assertThat(violations).hasSize(4);
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
