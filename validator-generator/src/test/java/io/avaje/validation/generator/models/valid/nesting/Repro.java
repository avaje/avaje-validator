package io.avaje.validation.generator.models.valid.nesting;

import io.avaje.validation.constraints.NotNull;
import io.avaje.validation.constraints.Valid;

public class Repro {
  @Valid
  public static class Top {
    @NotNull public Nested nested;

    public static record Nested(String prop) {}
  }
}
