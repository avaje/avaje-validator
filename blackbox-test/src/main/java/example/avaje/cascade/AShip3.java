package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.Valid;

import java.util.List;

@Valid
public record AShip3(
  @NotBlank String name,
  @NotEmpty List<ACrew> crew // not empty but no cascade validation
) { }
