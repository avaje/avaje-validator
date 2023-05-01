package io.avaje.validation.adapter;

import java.lang.annotation.Annotation;
import java.util.Map;

import io.avaje.validation.Validator;
import io.avaje.validation.core.MessageInterpolator;

public interface AnnotationValidationAdapter<T> extends ValidationAdapter<T> {

  //void validate(T type, ValidationRequest req);

  default AnnotationValidationAdapter<T> init(Map<String, Object> annotationValueMap) {
    return this;
  }

  /** Factory for creating a ValidationAdapter. */
  public interface Factory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     *
     * <p>Returning null means that the adapter could be created by another factory.
     */
    AnnotationValidationAdapter<?> create(
            Class<? extends Annotation> annotationType, Validator context, MessageInterpolator interpolator);
  }
}
