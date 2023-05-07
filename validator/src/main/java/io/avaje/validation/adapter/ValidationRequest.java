package io.avaje.validation.adapter;

public interface ValidationRequest {

  void addViolation(String msg, String propertyName);

  void addViolation(ValidationContext.Message msg, String propertyName);

  void pushPath(String path);

  void popPath();

  void throwWithViolations();

}
