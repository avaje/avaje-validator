package io.avaje.validation.generator.models.valid.subtypes.sealed;

import java.util.List;
import java.util.UUID;

import io.avaje.validation.constraints.NotEmpty;

public final class ByIdSelectorSealed implements SealedEntitySelector {

  @NotEmpty
  private List<@io.avaje.validation.generator.models.valid.typeconstraint.TypeConstrained UUID> ids;

  public void setIds(List<UUID> ids) {
    this.ids = ids;
  }

  public List<UUID> getIds() {
    return ids;
  }
}
