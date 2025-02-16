package example.avaje.subtypes.sealed;

import java.util.List;
import java.util.UUID;

import example.avaje.subtypes.sealed.SealedEntitySelector.NestedSealed;
import io.avaje.validation.ValidSubTypes;
import io.avaje.validation.constraints.NotEmpty;

@ValidSubTypes
public sealed interface SealedEntitySelector
    permits ByQuerySelectorSealed, ByIdSelectorSealed, NestedSealed {

  public final record NestedSealed(@NotEmpty List<UUID> ids) implements SealedEntitySelector {}
}
