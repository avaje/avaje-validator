package example.avaje.subtypes;

import jakarta.validation.Valid;

@Valid
public record SubtypeEntity(@Valid EntitySelector selector) {}
