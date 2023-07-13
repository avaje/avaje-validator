package io.avaje.validation.spi;

import io.avaje.validation.Validator;

/**
 * Callback interface that's used to customize a Validator.Builder.
 *
 * <p>These are service loaded when a Validator starts. The classes can be registered
 * with {@link BuilderCustomizer} or via a {@code provides} clause in module-info when using the java
 * module system.
 */
@FunctionalInterface
public interface ValidatorCustomizer {

  /** Callback to customize a Validator.Builder instance. */
  void customize(Validator.Builder builder);
}
