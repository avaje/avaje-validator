package example.avaje.positive;

import io.avaje.validation.constraints.NegativeOrZero;
import io.avaje.validation.constraints.Valid;

@Valid
public record APrimitiveNegativeOrZero(

  @NegativeOrZero byte abyte,
  @NegativeOrZero short ashort,
  @NegativeOrZero int aint,
  @NegativeOrZero long along,
  @NegativeOrZero double adouble,
  @NegativeOrZero float afloat

) {
}
