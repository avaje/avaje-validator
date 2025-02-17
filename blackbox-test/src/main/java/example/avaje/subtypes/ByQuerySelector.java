package example.avaje.subtypes;

import io.avaje.validation.constraints.NotBlank;

public final record ByQuerySelector(@NotBlank String query) implements EntitySelector {}
