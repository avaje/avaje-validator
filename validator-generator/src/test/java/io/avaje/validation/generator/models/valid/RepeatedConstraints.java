package io.avaje.validation.generator.models.valid;

import java.util.List;

import io.avaje.validation.constraints.Valid;
import jakarta.validation.constraints.Size;

@Valid
public record RepeatedConstraints(
    @Size(min = 1)
    @Size(max = 5)
    List<String> tags) {}
