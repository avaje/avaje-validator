package io.avaje.validation;

import java.util.Set;

import io.avaje.validation.stream.ConstraintViolation;


public interface ValidationType<T> {

  Set<ConstraintViolation> validate(T object);

  void validate(T object, Set<ConstraintViolation> violations);
}
