package example.avaje.past;

import io.avaje.validation.constraints.*;

import java.time.Year;

@Valid
public class APastFutureYear {

  @Past
  public Year past = Year.now().minusYears(1);
  @PastOrPresent
  public Year pastOrPresent = Year.now().minusYears(1);
  @Future
  public Year future = Year.now().plusYears(1);
  @FutureOrPresent
  public Year futureOrPresent = Year.now().plusYears(1);

  APastFutureYear makeInvalid() {
    past = Year.now().plusYears(1);
    pastOrPresent = Year.now().plusYears(1);
    future = Year.now().minusYears(1);
    futureOrPresent = Year.now().minusYears(1);
    return this;
  }

}
