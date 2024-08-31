package io.avaje.validation.generator.models.valid;

import java.util.List;

import io.avaje.validation.constraints.NotNull;
import io.avaje.validation.constraints.Valid;

@Valid
public record Recursive(
    @NotNull String name,
    @Valid @NotNull Recursive child,
    @Valid Recursive[] array,
    @Valid List<Recursive> list) {}
