package io.avaje.validation.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

public interface AdapterContext {

    <T> ValidationAdapter<T> adapter(Class<T> cls);

    <T> ValidationAdapter<T> adapter(Type type);

    <T> ValidationAdapter<T> adapter(Class<? extends Annotation> cls, Map<String, Object> attributes);

    String message(String key, Map<String, Object> attributes);

    /**
     * Factory for creating a ValidationAdapter.
     */
    interface AdapterFactory {

        /**
         * Create and return a ValidationAdapter given the type and annotations or return null.
         *
         * <p>Returning null means that the adapter could be created by another factory.
         */
        ValidationAdapter<?> create(Type type, AdapterContext ctx);
    }

    /**
     * Factory for creating a ValidationAdapter.
     */
    interface AnnotationFactory {

        /**
         * Create and return a ValidationAdapter given the type and annotations or return null.
         *
         * <p>Returning null means that the adapter could be created by another factory.
         */
        ValidationAdapter<?> create(
                Class<? extends Annotation> annotationType, AdapterContext ctx, Map<String, Object> attributes);
    }
}
