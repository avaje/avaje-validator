package example.avaje.range;

import io.avaje.validation.constraints.Max;
import io.avaje.validation.constraints.Valid;

import java.math.BigDecimal;
import java.math.BigInteger;

@Valid
public record AMaxCheck(
  @Max(4) Double aDouble,
  @Max(4) Float aFloat,
  @Max(4) BigDecimal decimal,
  @Max(4) BigInteger bigInt
) {
}
