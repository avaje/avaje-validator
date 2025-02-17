package io.avaje.validation.generator.models.valid.subtypes;

import io.avaje.validation.constraints.NotBlank;

public final record ByQuerySelector(@NotBlank String query) implements EntitySelector {}
