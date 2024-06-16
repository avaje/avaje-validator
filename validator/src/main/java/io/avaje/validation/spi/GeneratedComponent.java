package io.avaje.validation.spi;

/** Registers generated ValidationAdapters with the Validator.Builder */
@FunctionalInterface
public non-sealed interface GeneratedComponent extends ValidatorCustomizer, ValidatorExtension {}
