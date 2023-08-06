package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.YearMonth;

@Valid
public class APastFutureYearMonth {

  @Past
  public YearMonth past = YearMonth.now().minusMonths(1);
  @PastOrPresent
  public YearMonth pastOrPresent = YearMonth.now().minusMonths(1);
  @Future
  public YearMonth future = YearMonth.now().plusMonths(1);
  @FutureOrPresent
  public YearMonth futureOrPresent = YearMonth.now().plusMonths(1);

  APastFutureYearMonth makeInvalid() {
    past = YearMonth.now().plusMonths(1);
    pastOrPresent = YearMonth.now().plusMonths(1);
    future = YearMonth.now().minusMonths(1);
    futureOrPresent = YearMonth.now().minusMonths(1);
    return this;
  }

}
