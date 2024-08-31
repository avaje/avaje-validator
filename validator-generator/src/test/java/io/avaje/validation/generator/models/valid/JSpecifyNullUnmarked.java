package io.avaje.validation.generator.models.valid;

import org.jspecify.annotations.NullUnmarked;

import jakarta.validation.Valid;

@Valid
@NullUnmarked
public record JSpecifyNullUnmarked(String basic, String withMax, String withCustom) {}
