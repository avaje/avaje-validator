package example.avaje.range;

import io.avaje.validation.constraints.Max;
import io.avaje.validation.constraints.Valid;

@Valid
public record APrimitiveMax(

  @Max(3) byte abyte,
  @Max(3) short ashort,
  @Max(3) int aint,
  @Max(3) long along,
  @Max(3) double adouble,
  @Max(3) float afloat

) {
}
