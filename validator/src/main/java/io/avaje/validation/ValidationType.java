package io.avaje.validation;

import java.util.Collection;
import java.util.Set;


public interface ValidationType<T> {

  void validate(T object) throws ConstraintViolationException;

  void validateAll(Collection<T> collection);
}
