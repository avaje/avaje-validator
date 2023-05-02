package io.avaje.validation.adapter;


import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Factory for creating a ValidationAdapter.
 */
public interface AnnotationAdapterFactory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     *
     * <p>Returning null means that the adapter could be created by another factory.
     */
    ValidationAdapter<?> create(
            Class<? extends Annotation> annotationType, AdapterBuildContext ctx, Map<String, Object> attributes);
}
