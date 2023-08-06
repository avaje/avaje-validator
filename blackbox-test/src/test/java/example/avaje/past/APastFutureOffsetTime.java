package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.OffsetTime;

@Valid
public class APastFutureOffsetTime {

  @Past
  public OffsetTime past = OffsetTime.now().minusSeconds(60);
  @PastOrPresent
  public OffsetTime pastOrPresent = OffsetTime.now().minusSeconds(60);
  @Future
  public OffsetTime future = OffsetTime.now().plusSeconds(60);
  @FutureOrPresent
  public OffsetTime futureOrPresent = OffsetTime.now().plusSeconds(60);

  APastFutureOffsetTime makeInvalid() {
    past = OffsetTime.now().plusSeconds(60);
    pastOrPresent = OffsetTime.now().plusSeconds(60);
    future = OffsetTime.now().minusSeconds(60);
    futureOrPresent = OffsetTime.now().minusSeconds(60);
    return this;
  }

}
