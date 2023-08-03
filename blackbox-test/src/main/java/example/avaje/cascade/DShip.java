package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.Valid;

@Valid
public record DShip(
  @NotBlank String name,
  @NotBlank String rating,
  @NotEmpty String[] crew // cascade validation
) {
}
