package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotNull;
import io.avaje.validation.constraints.Valid;

@Valid
public record CascadeGroup(@Valid(groups = {CascadeGroup.class}) @NotNull Cascaded name) {

  public record Cascaded(@NotBlank(groups = {CascadeGroup.class}) String val) {}
}
