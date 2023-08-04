package example.avaje.range;

import io.avaje.validation.constraints.Range;
import jakarta.validation.Valid;

@Valid
public record AFloatRange(
  @Range(min = 1, max = 3)
  float value
) {
}
