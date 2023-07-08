package example.avaje;

import io.avaje.validation.constraints.*;

import java.time.LocalDate;

@Valid
public class APastFuture {

  @Past
  public LocalDate past = LocalDate.now().minusDays(1);
  @PastOrPresent
  public LocalDate pastOrPresent = LocalDate.now().minusDays(1);
  @Future
  public LocalDate future = LocalDate.now().plusDays(1);
  @FutureOrPresent
  public LocalDate futureOrPresent = LocalDate.now().plusDays(1);

}
