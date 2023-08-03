package example.avaje.crossfield;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Positive;
import io.avaje.validation.constraints.Valid;

@Valid
@APassingSkill
public record ATarnished(
  @NotBlank String name,
  @Positive int vigor,
  @Positive int endurance) {
}
