package example.avaje.decimal;

import io.avaje.validation.constraints.DecimalMax;
import io.avaje.validation.constraints.DecimalMin;
import io.avaje.validation.constraints.Valid;

import java.math.BigDecimal;

@Valid
public record ADecimalMax(

  @DecimalMax("4.5")
  BigDecimal maxInc,
  @DecimalMax(value = "4.5", inclusive = false)
  BigDecimal maxExc,
  @DecimalMin("4.5")
  BigDecimal minInc,
  @DecimalMin(value = "4.5", inclusive = false)
  BigDecimal minExc
) {
}
