package io.avaje.validation.spi;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

/** Factory for creating an Annotation Adapter for a given annotation. */
@FunctionalInterface
public non-sealed interface AnnotationFactory extends ValidationExtension {

  /**
   * Create and return a ValidationAdapter given the type and annotations or return null. Returning
   * null means that the adapter could be created by another factory.
   *
   * @param request Holds the details used to create the adapter
   * @return The created validation adapter or null if not applicable
   */
  ValidationAdapter<?> create(AdapterCreateRequest request);
}
