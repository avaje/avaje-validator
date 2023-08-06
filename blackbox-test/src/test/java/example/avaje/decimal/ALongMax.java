package example.avaje.decimal;

import io.avaje.validation.constraints.DecimalMax;
import io.avaje.validation.constraints.DecimalMin;
import io.avaje.validation.constraints.Valid;

@Valid
public record ALongMax(

  @DecimalMax("4.5")
  long maxInc,
  @DecimalMax(value = "4.5", inclusive = false)
  long maxExc,
  @DecimalMin("4.5")
  long minInc,
  @DecimalMin(value = "4.5", inclusive = false)
  long minExc
) {
}
