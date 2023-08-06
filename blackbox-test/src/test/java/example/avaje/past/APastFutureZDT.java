package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.ZonedDateTime;

@Valid
public class APastFutureZDT {

  @Past
  public ZonedDateTime past = ZonedDateTime.now().minusSeconds(60);
  @PastOrPresent
  public ZonedDateTime pastOrPresent = ZonedDateTime.now().minusSeconds(60);
  @Future
  public ZonedDateTime future = ZonedDateTime.now().plusSeconds(60);
  @FutureOrPresent
  public ZonedDateTime futureOrPresent = ZonedDateTime.now().plusSeconds(60);

  APastFutureZDT makeInvalid() {
    past = ZonedDateTime.now().plusSeconds(60);
    pastOrPresent = ZonedDateTime.now().plusSeconds(60);
    future = ZonedDateTime.now().minusSeconds(60);
    futureOrPresent = ZonedDateTime.now().minusSeconds(60);
    return this;
  }

}
