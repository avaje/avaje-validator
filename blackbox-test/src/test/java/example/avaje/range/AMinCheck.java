package example.avaje.range;

import io.avaje.validation.constraints.Min;
import io.avaje.validation.constraints.Valid;

import java.math.BigDecimal;
import java.math.BigInteger;

@Valid
public record AMinCheck(
  @Min(4) Double aDouble,
  @Min(4) Float aFloat,
  @Min(4) BigDecimal decimal,
  @Min(4) BigInteger bigInt
) {
}
