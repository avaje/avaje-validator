package example.avaje.range;

import io.avaje.validation.constraints.Range;
import io.avaje.validation.constraints.Valid;

@Valid
public record APrimitiveRange(

  @Range(min = 1, max = 3) byte abyte,
  @Range(min = 1, max = 3) short ashort,
  @Range(min = 1, max = 3) int aint,
  @Range(min = 1, max = 3) long along,
  @Range(min = 1, max = 3) double adouble,
  @Range(min = 1, max = 3) float afloat

) {
}
