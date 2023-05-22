package example.jakarta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@Valid
public class JPastFuture {

  @Past
  public LocalDate past = LocalDate.now().minusDays(1);
  @PastOrPresent
  public LocalDate pastOrPresent = LocalDate.now().minusDays(1);
  @Future
  public LocalDate future = LocalDate.now().plusDays(1);
  @FutureOrPresent
  public LocalDate futureOrPresent = LocalDate.now().plusDays(1);

}
