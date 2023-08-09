package example.avaje.range;

import io.avaje.validation.constraints.Min;
import io.avaje.validation.constraints.Valid;

@Valid
public record APrimitiveMin(

  @Min(3) byte abyte,
  @Min(3) short ashort,
  @Min(3) int aint,
  @Min(3) long along,
  @Min(3) double adouble,
  @Min(3) float afloat

) {
}
