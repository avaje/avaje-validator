package example.avaje.subtypes;

import java.util.List;
import java.util.UUID;

import io.avaje.validation.constraints.NotEmpty;

public final record ByIdSelector(@NotEmpty List<UUID> ids) implements EntitySelector {}
