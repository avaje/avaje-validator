package example.avaje.subtypes.sealed;

import jakarta.validation.Valid;

@Valid
public record SealedEntity(@Valid SealedEntitySelector selector) {}
