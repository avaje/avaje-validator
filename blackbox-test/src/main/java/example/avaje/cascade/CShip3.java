package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.Valid;

@Valid
public record CShip3(
  @NotBlank String name,
  @NotEmpty ACrew[] crew // NotEmpty with no cascade
) {
}
