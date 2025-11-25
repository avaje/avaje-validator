package io.avaje.validation.generator.models.valid.optional;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Positive;
import io.avaje.validation.constraints.Valid;

@Valid
public record CurseBearer(
    @NotBlank(message = "it'll happen to you too") Optional<String> name,
    @Positive OptionalInt estus,
    @Positive(message = "You Died") OptionalLong souls,
    @Positive(message = "you didn't pass the vigor check") OptionalDouble vigor,
    @Valid Optional<DarkSign> ds) {

  public record DarkSign(@NotBlank(message = "not cursed") String brand) {}
}
