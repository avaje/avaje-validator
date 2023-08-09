package example.avaje.positive;

import io.avaje.validation.constraints.Positive;
import io.avaje.validation.constraints.Valid;

@Valid
public record APrimitivePositive(

  @Positive byte abyte,
  @Positive short ashort,
  @Positive int aint,
  @Positive long along,
  @Positive double adouble,
  @Positive float afloat

) {
}
