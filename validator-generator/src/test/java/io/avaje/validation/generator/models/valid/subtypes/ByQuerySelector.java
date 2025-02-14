package io.avaje.validation.generator.models.valid.subtypes;

import javax.validation.constraints.NotBlank;

public final class ByQuerySelector implements EntitySelector {

  @NotBlank private String query;

  public void setQuery(String query) {
    this.query = query;
  }

  public String getQuery() {
    return query;
  }
}
