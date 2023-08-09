package example.avaje.positive;

import io.avaje.validation.constraints.Negative;
import io.avaje.validation.constraints.Valid;

@Valid
public record APrimitiveNegative(

  @Negative byte abyte,
  @Negative short ashort,
  @Negative int aint,
  @Negative long along,
  @Negative double adouble,
  @Negative float afloat

) {
}
