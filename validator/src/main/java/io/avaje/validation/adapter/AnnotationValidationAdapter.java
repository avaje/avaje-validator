package io.avaje.validation.adapter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import io.avaje.validation.Validator;
import io.avaje.validation.core.MessageInterpolator;

public interface AnnotationValidationAdapter<T> extends ValidationAdapter<T> {

  //void validate(T type, ValidationRequest req);

  default AnnotationValidationAdapter<T> init(Map<String, String> annotationValueMap) {
    return this;
  }

  default AnnotationValidationAdapter<T> andThen(AnnotationValidationAdapter<? super T> after) {
    Objects.requireNonNull(after);
    return (t, v) -> {
      if (validate(t, v)) {
        return after.validate(t, v);
      }
      return true;
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
