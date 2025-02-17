package example.avaje.subtypes.sealed;

import io.avaje.validation.constraints.NotBlank;

public final record ByQuerySelectorSealed(@NotBlank String query) implements SealedEntitySelector {}
