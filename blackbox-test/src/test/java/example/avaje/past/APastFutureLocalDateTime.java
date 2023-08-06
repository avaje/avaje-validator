package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.LocalDateTime;

@Valid
public class APastFutureLocalDateTime {

  @Past
  public LocalDateTime past = LocalDateTime.now().minusDays(1);
  @PastOrPresent
  public LocalDateTime pastOrPresent = LocalDateTime.now().minusDays(1);
  @Future
  public LocalDateTime future = LocalDateTime.now().plusDays(1);
  @FutureOrPresent
  public LocalDateTime futureOrPresent = LocalDateTime.now().plusDays(1);

  APastFutureLocalDateTime makeInvalid() {
    past = LocalDateTime.now().plusDays(1);
    pastOrPresent = LocalDateTime.now().plusDays(1);
    future = LocalDateTime.now().minusDays(1);
    futureOrPresent = LocalDateTime.now().minusDays(1);
    return this;
  }

}
