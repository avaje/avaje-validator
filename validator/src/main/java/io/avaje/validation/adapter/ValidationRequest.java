package io.avaje.validation.adapter;

import io.avaje.lang.Nullable;

public interface ValidationRequest {

  void addViolation(ValidationContext.Message msg, String propertyName, @Nullable Object value);

  void pushPath(String path);

  void popPath();

  void throwWithViolations();

}
