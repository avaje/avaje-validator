package example.avaje.decimal;

import io.avaje.validation.constraints.DecimalMax;
import io.avaje.validation.constraints.DecimalMin;
import io.avaje.validation.constraints.Valid;

@Valid
public record AFloatMax(

  @DecimalMax("4.5")
  float maxInc,
  @DecimalMax(value = "4.5", inclusive = false)
  float maxExc,
  @DecimalMin("4.5")
  float minInc,
  @DecimalMin(value = "4.5", inclusive = false)
  float minExc
) {
}
