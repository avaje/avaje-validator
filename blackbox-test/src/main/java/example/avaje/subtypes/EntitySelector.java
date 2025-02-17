package example.avaje.subtypes;

import io.avaje.validation.ValidSubTypes;

@ValidSubTypes({ByQuerySelector.class, ByIdSelector.class})
public interface EntitySelector {}
