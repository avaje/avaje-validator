package example.avaje.subtypes.sealed;

import java.util.List;
import java.util.UUID;

import example.avaje.subtypes.sealed.SealedEntitySelector.NestedSealed;
import io.avaje.validation.SubTypes;
import io.avaje.validation.constraints.NotEmpty;

@SubTypes
public sealed interface SealedEntitySelector
    permits ByQuerySelectorSealed, ByIdSelectorSealed, NestedSealed {

  public final record NestedSealed(@NotEmpty List<UUID> ids) implements SealedEntitySelector {}
}
