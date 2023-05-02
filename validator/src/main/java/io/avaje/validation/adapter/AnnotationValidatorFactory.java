package io.avaje.validation.adapter;

import io.avaje.validation.core.MessageInterpolator;

import java.lang.annotation.Annotation;

/**
 * Factory for creating a ValidationAdapter.
 */
public interface AnnotationValidatorFactory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     *
     * <p>Returning null means that the adapter could be created by another factory.
     */
    ValidationAdapter<?> create(
            Class<? extends Annotation> annotationType, AdapterBuildContext ctx, MessageInterpolator interpolator);
}
