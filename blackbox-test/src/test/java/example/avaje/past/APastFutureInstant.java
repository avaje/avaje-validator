package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.Instant;

@Valid
public class APastFutureInstant {

  @Past
  public Instant past = Instant.now().minusSeconds(60);
  @PastOrPresent
  public Instant pastOrPresent = Instant.now().minusSeconds(60);
  @Future
  public Instant future = Instant.now().plusSeconds(60);
  @FutureOrPresent
  public Instant futureOrPresent = Instant.now().plusSeconds(60);

  APastFutureInstant makeInvalid() {
    past = Instant.now().plusSeconds(60);
    pastOrPresent = Instant.now().plusSeconds(60);
    future = Instant.now().minusSeconds(60);
    futureOrPresent = Instant.now().minusSeconds(60);
    return this;
  }

}
