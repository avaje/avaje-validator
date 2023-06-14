package io.avaje.validation;

import java.util.Set;

/**
 * Exception holding a set of constraint violations.
 */
public final class ConstraintViolationException extends RuntimeException {

  private final Set<ConstraintViolation> violations;

  /** Create with the given constraint violations */
  public ConstraintViolationException(Set<ConstraintViolation> violations) {
    this.violations = violations;
  }

  /** Return the constraint violations. */
  public Set<ConstraintViolation> violations() {
    return violations;
  }
}
