package io.avaje.validation;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.avaje.validation.adapter.ValidationRequest;
import io.avaje.validation.core.MessageInterpolator;

public interface AnnotationValidationAdapter<T> {

  void validate(T type, ValidationRequest req);

  default AnnotationValidationAdapter<T> init(Map<String, String> annotationValueMap) {
    return this;
  }

  default AnnotationValidationAdapter<T> andThen(AnnotationValidationAdapter<? super T> after) {
    Objects.requireNonNull(after);
    return (t, v) -> {
      validate(t, v);
      after.validate(t, v);
    };
  }
  /** Factory for creating a ValidationAdapter. */
  public interface Factory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     *
     * <p>Returning null means that the adapter could be created by another factory.
     */
    AnnotationValidationAdapter<?> create(
        Type annotationType, Validator context, MessageInterpolator interpolator);
  }
}
