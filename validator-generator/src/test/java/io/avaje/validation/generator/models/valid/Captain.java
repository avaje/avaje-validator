package io.avaje.validation.generator.models.valid;

import io.avaje.validation.constraints.NotNull;
import jakarta.validation.Valid;

public class Captain {

  private String name;

  @NotNull(message = "Not captain level")
  private Bankai bankai;

  public String name() {
    return name;
  }

  public Bankai bankai() {
    return bankai;
  }

  public record Bankai(String name, int forceMultiplier) {}
}
