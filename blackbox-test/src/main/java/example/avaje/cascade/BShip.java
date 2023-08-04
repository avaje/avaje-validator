package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

import java.util.Set;

@Valid
public record BShip(
  @NotBlank String name,
  @Valid Set<ACrew> crew // cascade validation
) { }
