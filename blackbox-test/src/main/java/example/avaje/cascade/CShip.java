package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@Valid
public record CShip(
  @NotBlank String name,
  @Valid ACrew[] crew // cascade validation
) {
}
