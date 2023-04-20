package io.avaje.validation;

/**
 * User defined components to register custom JsonAdapters with Validator.Builder.
 * <p>
 * These are service loaded when Validator starts. They can be specified in
 * {@code META-INF/services/io.avaje.validation.ValidatorComponent} or when using
 * java module system via a {@code provides} clause in module-info.
 */
@FunctionalInterface
public interface ValidatorComponent {

  /**
   * Register JsonAdapters with the Builder.
   */
  void register(Validator.Builder builder);
}
