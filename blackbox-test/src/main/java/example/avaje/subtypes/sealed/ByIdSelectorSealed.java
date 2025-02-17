package example.avaje.subtypes.sealed;

import java.util.List;
import java.util.UUID;

import io.avaje.validation.constraints.NotEmpty;

public final class ByIdSelectorSealed implements SealedEntitySelector {

  @NotEmpty private final List<UUID> ids;

  public ByIdSelectorSealed(@NotEmpty List<UUID> ids) {
    this.ids = ids;
  }

  public List<UUID> getIds() {
    return ids;
  }
}
