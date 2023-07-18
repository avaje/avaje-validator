package example.avaje;

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

  ConstraintViolation one(Object any) {
    return one(any, Locale.ENGLISH);
  }

  ConstraintViolation one(Object any, Locale locale) {

    final var violations = new ArrayList<>(validator.validate(any, locale));

    if (violations.isEmpty()) throw new IllegalStateException();

    assertThat(violations).hasSize(1);
    return violations.get(0);
  }
}
