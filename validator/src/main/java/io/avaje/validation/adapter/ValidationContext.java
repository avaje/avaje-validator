package io.avaje.validation.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

public interface ValidationContext {

    /**
     * Return the adapter for the given type.
     */
    <T> ValidationAdapter<T> adapter(Class<T> cls);

    /**
     * Return the adapter for the given type.
     */
    <T> ValidationAdapter<T> adapter(Type type);

    /**
     * Return the adapter for the given annotation with attributes.
     */
    <T> ValidationAdapter<T> adapter(Class<? extends Annotation> cls, Map<String, Object> attributes);

    /**
     * Return the message to use given the global key and attributes.
     * @param key Used to lookup the fallback default message format to use
     * @param attributes Attributes that could contain user defined message to use
     */
    String message(String key, Map<String, Object> attributes);

  /**
   * Return the message object held by a validation adapter
   */
  Message adapterMessage(String key, Map<String, Object> attributes);

    interface Message {

      String template();

      Map<String, Object> attributes();
    }

    /**
     * Factory for creating a ValidationAdapter for a given type.
     */
    interface AdapterFactory {

        /**
         * Create and return a ValidationAdapter given the type and annotations or return null.
         *
         * <p>Returning null means that the adapter could be created by another factory.
         */
        ValidationAdapter<?> create(Type type, ValidationContext ctx);
    }

    /**
     * Factory for creating a ValidationAdapter for a given annotation.
     */
    interface AnnotationFactory {

        /**
         * Create and return a ValidationAdapter given the type and annotations or return null.
         *
         * <p>Returning null means that the adapter could be created by another factory.
         */
        ValidationAdapter<?> create(
                Class<? extends Annotation> annotationType, ValidationContext ctx, Map<String, Object> attributes);
    }
}
