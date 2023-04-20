package io.avaje.validation.core;

import java.lang.reflect.Type;
import java.util.Map;

import io.avaje.validation.Validator;
import io.avaje.validation.stream.ConstraintViolation;

public interface AnnotationValidationAdapter<T> {

  ConstraintViolation validate(T type);

  AnnotationValidationAdapter<T> init(Map<String, String> annotationValueMap);

  /** Factory for creating a ValidationAdapter. */
  public interface Factory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     *
     * <p>Returning null means that the adapter could be created by another factory.
     */
    AnnotationValidationAdapter<?> create(Type type, Validator jsonb, MessageInterpolator interpolator);
  }
}
