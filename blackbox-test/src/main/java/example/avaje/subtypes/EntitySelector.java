package example.avaje.subtypes;

import io.avaje.validation.SubTypes;

@SubTypes({ByQuerySelector.class, ByIdSelector.class})
public interface EntitySelector {}
