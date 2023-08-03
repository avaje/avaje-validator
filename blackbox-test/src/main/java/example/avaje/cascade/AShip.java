package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

import java.util.List;

@Valid
public record AShip(
  @NotBlank String name,
  @Valid List<ACrew> crew // cascade validation
) { }
