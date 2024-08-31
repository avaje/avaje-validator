package example.avaje.jspecify;

import org.jspecify.annotations.Nullable;

import jakarta.validation.Valid;

@Valid
public record JSpecifyNotNull(String basic, String withMax, @Nullable String withCustom) {}
