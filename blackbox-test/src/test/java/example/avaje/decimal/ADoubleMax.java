package example.avaje.decimal;

import io.avaje.validation.constraints.DecimalMax;
import io.avaje.validation.constraints.DecimalMin;
import io.avaje.validation.constraints.Valid;

import java.math.BigDecimal;

@Valid
public record ADoubleMax(

  @DecimalMax("4.5")
  Double maxInc,
  @DecimalMax(value = "4.5", inclusive = false)
  Double maxExc,
  @DecimalMin("4.5")
  Double minInc,
  @DecimalMin(value = "4.5", inclusive = false)
  Double minExc
) {
}
