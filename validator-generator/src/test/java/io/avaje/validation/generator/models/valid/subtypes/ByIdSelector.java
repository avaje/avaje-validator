package io.avaje.validation.generator.models.valid.subtypes;

import java.util.List;
import java.util.UUID;

import io.avaje.validation.constraints.NotEmpty;

public final class ByIdSelector implements EntitySelector {

  @NotEmpty
  private List<UUID> ids;

  public void setIds(List<UUID> ids) {
    this.ids = ids;
  }

  public List<UUID> getIds() {
    return ids;
  }
}
