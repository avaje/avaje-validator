package io.avaje.validation;

import java.util.Collection;


public interface ValidationType<T> {

  void validate(T object) throws ConstraintViolationException;

  //void validateAll(Collection<T> collection);
}
