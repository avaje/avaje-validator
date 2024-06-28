package example.avaje.mixin;

import io.avaje.validation.constraints.NotNull;

public record Captain(String name, @NotNull(message = "Not captain level") Bankai bankai) {

  public record Bankai(String name) {}
}
