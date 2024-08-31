package example.avaje.cascade;

import io.avaje.validation.constraints.NotNull;
import io.avaje.validation.constraints.Valid;

@Valid
public record Recursive(@NotNull String name, @Valid @NotNull Recursive child) {}
