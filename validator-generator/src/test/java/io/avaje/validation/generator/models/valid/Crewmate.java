package io.avaje.validation.generator.models.valid;

import java.util.List;

import io.avaje.validation.ValidPojo;
import io.avaje.validation.constraints.NotEmpty;

@ValidPojo
public record Crewmate(@NotEmpty List<String> taskNames) {}
