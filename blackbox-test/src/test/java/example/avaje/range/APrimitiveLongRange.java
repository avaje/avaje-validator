package example.avaje.range;

import io.avaje.validation.constraints.Range;
import jakarta.validation.Valid;

@Valid
public record APrimitiveLongRange(
  @Range(min = 1, max = 3)
  long value
) {
}
