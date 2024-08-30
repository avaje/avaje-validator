package example.avaje.jspecify;

import org.jspecify.annotations.Nullable;

import io.avaje.validation.constraints.NotBlank;
import jakarta.validation.Valid;

@Valid
public record JSpecifyNotNull(
    @NotBlank String basic, @NotBlank String withMax, @Nullable @NotBlank String withCustom) {}
