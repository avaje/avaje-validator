package io.avaje.validation.generator.models.valid.subtypes.sealed;

import javax.validation.constraints.NotBlank;

public final class ByQuerySelectorSealed implements SealedEntitySelector {

  @NotBlank private String query;

  public void setQuery(String query) {
    this.query = query;
  }

  public String getQuery() {
    return query;
  }
}
