package io.avaje.validation;

import java.util.Set;

/**
 * Exception holding a set of constraint violations.
 */
public final class ConstraintViolationException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private final transient Set<ConstraintViolation> violations;

  /** Create with the given constraint violations */
  public ConstraintViolationException(Set<ConstraintViolation> violations) {
    this.violations = violations;
  }

  /** Return the constraint violations. */
  public Set<ConstraintViolation> violations() {
    return violations;
  }
}
