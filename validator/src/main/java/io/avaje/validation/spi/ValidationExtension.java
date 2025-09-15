package io.avaje.validation.spi;

import io.avaje.spi.Service;

/**
 * Marker super interface for all validation Service Provider Interfaces (SPIs).
 *
 * <p>Implementations of this interface extend the validation system with custom adapters,
 * annotation handling, message interpolation, and other validation-related features.
 *
 * <p>Permitted subtypes include:
 *
 * <ul>
 *   <li>{@link AdapterFactory} - Provides adapters for validation logic.
 *   <li>{@link AnnotationFactory} - Provides adapters for annotations.
 *   <li>{@link GeneratedComponent} - Registry of generated validation adapters.
 *   <li>{@link MessageInterpolator} - Interpolates validation messages.
 *   <li>{@link ValidatorCustomizer} - Allows customization of the validator instance.
 * </ul>
 */
@Service
public sealed interface ValidationExtension
    permits AdapterFactory,
        AnnotationFactory,
        GeneratedComponent,
        MessageInterpolator,
        ValidatorCustomizer {}
