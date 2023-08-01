package io.avaje.validation.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.avaje.lang.Nullable;

/** Context used to lookup validation adapters and create validation requests. */
public interface ValidationContext {

  /**
   * Return the adapter for the given type.
   *
   * @param cls The class for which the adapter is requested
   * @param <T> The type this adapter validates
   * @return The validation adapter for the given type
   */
  <T> ValidationAdapter<T> adapter(Class<T> cls);

  /**
   * Return the adapter for the given type.
   *
   * @param type The type for which the adapter is requested
   * @param <T> The type this adapter validates
   * @return The validation adapter for the given type
   */
  <T> ValidationAdapter<T> adapter(Type type);

  /**
   * Return the constraint adapter for the given annotation with attributes.
   *
   * @param cls The annotation class
   * @param attributes The attributes associated with the annotation
   * @param <T> The type this adapter validates
   * @return The validation adapter for the given annotation with attributes
   */
  <T> ValidationAdapter<T> adapter(Class<? extends Annotation> cls, Map<String, Object> attributes);

  /**
   * Return the constraint adapter for the given annotation with attributes. Used for adapters that combine
   * multiple annotation adapters.
   *
   * @param cls The class representing the annotation type
   * @param groups The validation groups associated with the annotation
   * @param message The error message associated with the annotation
   * @param attributes The attributes associated with the annotation
   * @param <T> The type this adapter validates
   * @return The validation adapter for the given annotation with attributes
   */
  <T> ValidationAdapter<T> adapter(
      Class<? extends Annotation> cls,
      Set<Class<?>> groups,
      String message,
      Map<String, Object> attributes);

  /**
   * Return a no-op adapter.
   *
   * @param <T> The type this adapter validates
   * @return The no-op validation adapter
   */
  <T> ValidationAdapter<T> noop();

  /**
   * Create a message object using the annotation attribute "message".
   *
   * @param attributes The attributes associated with the annotation
   * @return The message object
   */
  Message message(Map<String, Object> attributes);

  /**
   * Create a message object using the given string.
   *
   * @param message The error message
   * @param attributes The attributes associated with the annotation
   * @return The message object created using the given string and annotation attributes
   */
  Message message(String message, Map<String, Object> attributes);

  /**
   * Create a validation request with the specified locale and groups.
   *
   * @param locale The locale for the validation request
   * @param groups The validation groups for the request
   * @return The validation request with the specified locale and groups
   */
  ValidationRequest request(@Nullable Locale locale, List<Class<?>> groups);

  /** Represents a message object used in error message interpolation. */
  interface Message {

    /**
     * Get the template for the message. A lookup will be performed on the configured resource
     * bundles to interpolate the message
     *
     * @return The template for the message
     */
    String template();

    /**
     * Get the annotation attributes associated with the message.
     *
     * @return The annotation attributes associated with the message
     */
    Map<String, Object> attributes();

    /**
     * Get the lookup key for the message.
     *
     * @return The template for the message + a unique number for deduplication purposes.
     */
    String lookupkey();
  }

  /** Factory for creating a ValidationAdapter for a given type. */
  @FunctionalInterface
  interface AdapterFactory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     * Returning null means that the adapter could be created by another factory.
     *
     * @param type The type for which the adapter is being created
     * @param ctx The validation context
     * @return The created validation adapter or null if not applicable
     */
    ValidationAdapter<?> create(Type type, ValidationContext ctx);
  }

  /** Factory for creating an Annotation Adapter for a given annotation. */
  @FunctionalInterface
  interface AnnotationFactory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     * Returning null means that the adapter could be created by another factory.
     *
     * @param annotationType The annotation type for which the adapter is being created
     * @param ctx The validation context
     * @param groups The validation groups associated with the annotation
     * @param attributes The attributes associated with the annotation
     * @return The created validation adapter or null if not applicable
     */
    ValidationAdapter<?> create(AdapterCreateRequest request);
  }

  /** Request to create a Validation Adapter */
  interface AdapterCreateRequest {

    ValidationContext ctx();

    Class<? extends Annotation> annotationType();

    Set<Class<?>> groups();

    Map<String, Object> attributes();

    Object attribute(String key);

    Message message();

    Message message(String key);

    String targetType();

    AdapterCreateRequest withValue(long value);
  }
}
