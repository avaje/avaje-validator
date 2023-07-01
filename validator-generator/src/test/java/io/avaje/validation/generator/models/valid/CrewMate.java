package io.avaje.validation.generator.models.valid;

import java.util.List;

import io.avaje.validation.constraints.NotEmpty;

public record CrewMate(@NotEmpty List<String> assignedTasks) {}
