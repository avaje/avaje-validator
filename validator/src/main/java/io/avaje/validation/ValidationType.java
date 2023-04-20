package io.avaje.validation;

public interface ValidationType<T> {

  void validate(T object) throws ConstraintViolationException;
}
