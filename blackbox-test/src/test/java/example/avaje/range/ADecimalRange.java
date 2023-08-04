package example.avaje.range;

import io.avaje.validation.constraints.Range;
import jakarta.validation.Valid;

import java.math.BigDecimal;

@Valid
public record ADecimalRange(
  @Range(min = 1, max = 3)
  BigDecimal decimal
) {
}
