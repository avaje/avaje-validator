package io.avaje.validation.adapter;

import java.util.Map;

public interface AnnotationValidationAdapter<T> extends ValidationAdapter<T> {


  default AnnotationValidationAdapter<T> init(Map<String, Object> annotationValueMap) {
    return this;
  }

}
