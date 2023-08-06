package example.avaje.positive;

import io.avaje.validation.constraints.*;

@Valid
public record APositiveFloat(

  @Positive
  Float pos,

  @PositiveOrZero
  Float posOrZero,

  @Negative
  Float neg,

  @NegativeOrZero
  Float negOrZero
) {
}
