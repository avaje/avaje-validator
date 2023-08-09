package example.avaje.positive;

import io.avaje.validation.constraints.PositiveOrZero;
import io.avaje.validation.constraints.Valid;

@Valid
public record APrimitivePositiveOrZero(

  @PositiveOrZero byte abyte,
  @PositiveOrZero short ashort,
  @PositiveOrZero int aint,
  @PositiveOrZero long along,
  @PositiveOrZero double adouble,
  @PositiveOrZero float afloat

) {
}
