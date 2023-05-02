package io.avaje.validation.adapter;

import io.avaje.validation.Validator;

/**
 * User defined components to register custom ValidationAdapters with Validator.Builder.
 * <p>
 * These are service loaded when Validator starts. They can be specified in
 * {@code META-INF/services/io.avaje.validation.adapter.ValidatorComponent} or when using
 * java module system via a {@code provides} clause in module-info.
 */
@FunctionalInterface
public interface ValidatorComponent {

  /**
   * Register ValidationAdapters with the builder.
   */
  void register(Validator.Builder builder);
}
