package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.Valid;

@Valid
public record CShip2(
  @NotBlank String name,
  @Valid @NotEmpty ACrew[] crew // cascade validation
) {
}
