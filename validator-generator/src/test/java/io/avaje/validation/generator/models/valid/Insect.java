package io.avaje.validation.generator.models.valid;

import java.util.Set;

import org.jspecify.annotations.NullMarked;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Size;
import jakarta.validation.Valid;

@Valid
@NullMarked
public record Insect(@NotBlank @Size(min = 1, max = 50) String name) {
  private static final Set<String> FLYING = Set.of("Fly", "Butterfly");
  private static final Set<String> WALKING = Set.of("Ant", "Stick insect");

  public Set<String> associated() {
    if (FLYING.contains(name)) {
      return FLYING;
    }
    return WALKING;
  }
}
