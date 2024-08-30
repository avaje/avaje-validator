package io.avaje.validation.generator.models.valid;

import io.avaje.validation.constraints.Valid;

@Valid
public record Recursive( String name, @Valid Recursive child) {}
