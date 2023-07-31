package example.avaje.range;

import io.avaje.validation.constraints.Length;
import io.avaje.validation.constraints.Range;
import jakarta.validation.Valid;

@Valid
public record ARange(
  @Range(min = 1, max = 3)
  int anIntVal,
  @Range(min = 1, max = 3)
  long aLongVal
) {
}
