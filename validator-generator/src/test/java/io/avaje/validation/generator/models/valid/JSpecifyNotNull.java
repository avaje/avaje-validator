package io.avaje.validation.generator.models.valid;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import io.avaje.validation.constraints.NotBlank;
import jakarta.validation.Valid;

@Valid
@NullMarked
public record JSpecifyNotNull(
    @NotBlank String basic, @NotBlank String withMax, @Nullable @NotBlank String withCustom) {}
