package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.LocalTime;

@Valid
public class APastFutureLocalTime {

  @Past
  public LocalTime past = LocalTime.now().minusMinutes(1);
  @PastOrPresent
  public LocalTime pastOrPresent = LocalTime.now().minusMinutes(1);
  @Future
  public LocalTime future = LocalTime.now().plusMinutes(1);
  @FutureOrPresent
  public LocalTime futureOrPresent = LocalTime.now().plusMinutes(1);

  APastFutureLocalTime makeInvalid() {
    past = LocalTime.now().plusMinutes(1);
    pastOrPresent = LocalTime.now().plusMinutes(1);
    future = LocalTime.now().minusMinutes(1);
    futureOrPresent = LocalTime.now().minusMinutes(1);
    return this;
  }

}
