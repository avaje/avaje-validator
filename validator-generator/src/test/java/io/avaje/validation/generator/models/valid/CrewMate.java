package io.avaje.validation.generator.models.valid;

import java.util.List;

public record CrewMate(
    @Combining(
            message = "combined",
            groups = {Character.class})
        List<String> assignedTasks) {}
