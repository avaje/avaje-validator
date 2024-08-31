package io.avaje.validation.generator.models.valid;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import jakarta.validation.Valid;

@Valid
@NullMarked
public record JSpecifyNotNull(String basic, String withMax, @Nullable String withCustom) {}
