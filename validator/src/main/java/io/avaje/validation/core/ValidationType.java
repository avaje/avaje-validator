package io.avaje.validation.core;

import io.avaje.validation.ConstraintViolationException;

public interface ValidationType<T> {

  void validate(T object) throws ConstraintViolationException;
}
