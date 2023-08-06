package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.LocalDate;

@Valid
public class APastFutureLocalDate {

  @Past
  public LocalDate past = LocalDate.now().minusDays(1);
  @PastOrPresent
  public LocalDate pastOrPresent = LocalDate.now().minusDays(1);
  @Future
  public LocalDate future = LocalDate.now().plusDays(1);
  @FutureOrPresent
  public LocalDate futureOrPresent = LocalDate.now().plusDays(1);

  APastFutureLocalDate makeInvalid() {
    past = LocalDate.now().plusDays(1);
    pastOrPresent = LocalDate.now().plusDays(1);
    future = LocalDate.now().minusDays(1);
    futureOrPresent = LocalDate.now().minusDays(1);
    return this;
  }

}
