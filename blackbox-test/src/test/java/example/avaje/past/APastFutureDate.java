package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.Instant;
import java.util.Date;

@Valid
public class APastFutureDate {

  @Past
  public Date past = Date.from(Instant.now().minusSeconds(60));
  @PastOrPresent
  public Date pastOrPresent = Date.from(Instant.now().minusSeconds(60));
  @Future
  public Date future = Date.from(Instant.now().plusSeconds(60));
  @FutureOrPresent
  public Date futureOrPresent = Date.from(Instant.now().plusSeconds(60));

  APastFutureDate makeInvalid() {
    past = Date.from(Instant.now().plusSeconds(60));
    pastOrPresent = Date.from(Instant.now().plusSeconds(60));
    future = Date.from(Instant.now().minusSeconds(60));
    futureOrPresent = Date.from(Instant.now().minusSeconds(60));
    return this;
  }

}
