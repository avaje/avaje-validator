package io.avaje.validation.spi;

import java.lang.reflect.Type;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;

/** Factory for creating a ValidationAdapter for a given type. */
@FunctionalInterface
public non-sealed interface AdapterFactory extends ValidationExtension {

  /**
   * Create and return a ValidationAdapter given the type and annotations or return null. Returning
   * null means that the adapter could be created by another factory.
   *
   * @param type The type for which the adapter is being created
   * @param ctx The validation context
   * @return The created validation adapter or null if not applicable
   */
  ValidationAdapter<?> create(Type type, ValidationContext ctx);
}
