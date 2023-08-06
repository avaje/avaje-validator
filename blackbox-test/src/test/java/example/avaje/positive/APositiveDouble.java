package example.avaje.positive;

import io.avaje.validation.constraints.*;

@Valid
public record APositiveDouble(

  @Positive
  Double pos,

  @PositiveOrZero
  Double posOrZero,

  @Negative
  Double neg,

  @NegativeOrZero
  Double negOrZero
) {
}
