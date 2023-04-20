package io.avaje.validation;

import java.util.Set;


public interface ValidationType<T> {

  Set<ConstraintViolation> validate(T object);

  void validate(T object, Set<ConstraintViolation> violations);
}
