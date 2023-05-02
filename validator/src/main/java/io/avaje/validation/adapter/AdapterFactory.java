package io.avaje.validation.adapter;

import java.lang.reflect.Type;

/**
 * Factory for creating a ValidationAdapter.
 */
public interface AdapterFactory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     *
     * <p>Returning null means that the adapter could be created by another factory.
     */
    ValidationAdapter<?> create(Type type, AdapterBuildContext ctx);
}
