package io.avaje.validation.adapter;

import java.util.List;
import java.util.Set;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;

/** A validation request. */
public interface ValidationRequest {

  /** The groups tied to this ValidationRequest */
  List<Class<?>> groups();

  /**
   * Add a constraint violation for the given property.
   *
   * @param message The message
   * @param propertyName The property that failed the constraint
   */
  void addViolation(ValidationContext.Message message, String propertyName);

  /** Push the nested property path. */
  void pushPath(String path);

  /** Pop the nested property path. */
  void popPath();

  Set<ConstraintViolation> violations();

  /** Throw ConstraintViolationException if there are violations in this request. */
  void throwWithViolations() throws ConstraintViolationException;

}
