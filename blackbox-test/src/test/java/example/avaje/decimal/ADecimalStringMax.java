package example.avaje.decimal;

import io.avaje.validation.constraints.DecimalMax;
import io.avaje.validation.constraints.DecimalMin;
import io.avaje.validation.constraints.Valid;

import java.math.BigDecimal;

@Valid
public record ADecimalStringMax(

  @DecimalMax("4.5")
  String maxInc,
  @DecimalMax(value = "4.5", inclusive = false)
  String maxExc,
  @DecimalMin("4.5")
  String minInc,
  @DecimalMin(value = "4.5", inclusive = false)
  String minExc
) {
}
