package example.avaje.typeuse;

import java.util.List;

import io.avaje.validation.constraints.NotEmpty;

public record CrewMate(@NotEmpty(message = "Must have valid task") String assignedTasks) {}
