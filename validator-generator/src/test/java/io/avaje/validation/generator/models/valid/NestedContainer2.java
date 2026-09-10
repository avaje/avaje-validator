package io.avaje.validation.generator.models.valid;

import java.util.List;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@Valid
public record NestedContainer2(
    @Valid List<@Valid List<@NotBlank String>> matrix,
    List<List<List<@NotBlank String>>> cube) {}
