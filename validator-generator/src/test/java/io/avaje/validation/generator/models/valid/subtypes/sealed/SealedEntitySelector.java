package io.avaje.validation.generator.models.valid.subtypes.sealed;

import java.util.List;
import java.util.UUID;

import io.avaje.validation.ValidSubTypes;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.generator.models.valid.subtypes.sealed.SealedEntitySelector.NestedSealed;

@ValidSubTypes
public sealed interface SealedEntitySelector
    permits ByQuerySelectorSealed, ByIdSelectorSealed, NestedSealed {

  public final record NestedSealed(@NotEmpty List<UUID> ids) implements SealedEntitySelector {}
}
