package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.OffsetDateTime;

@Valid
public class APastFutureODT {

  @Past
  public OffsetDateTime past = OffsetDateTime.now().minusSeconds(60);
  @PastOrPresent
  public OffsetDateTime pastOrPresent = OffsetDateTime.now().minusSeconds(60);
  @Future
  public OffsetDateTime future = OffsetDateTime.now().plusSeconds(60);
  @FutureOrPresent
  public OffsetDateTime futureOrPresent = OffsetDateTime.now().plusSeconds(60);

  APastFutureODT makeInvalid() {
    past = OffsetDateTime.now().plusSeconds(60);
    pastOrPresent = OffsetDateTime.now().plusSeconds(60);
    future = OffsetDateTime.now().minusSeconds(60);
    futureOrPresent = OffsetDateTime.now().minusSeconds(60);
    return this;
  }

}
