package example.avaje.range;

import io.avaje.validation.constraints.Max;
import io.avaje.validation.constraints.Min;
import io.avaje.validation.constraints.Valid;

@Valid
public record APrimitiveMinMax(

  @Min(1) @Max(3) byte abyte,
  @Min(1) @Max(3) short ashort,
  @Min(1) @Max(3) int aint,
  @Min(1) @Max(3) long along,
  @Min(1) @Max(3) double adouble,
  @Min(1) @Max(3) float afloat

) {
}
