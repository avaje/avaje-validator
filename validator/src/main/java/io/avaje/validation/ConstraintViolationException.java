package io.avaje.validation;

import java.io.Serial;
import java.util.List;
import java.util.Set;

/** Exception holding a set of constraint violations. */
public final class ConstraintViolationException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final transient Set<ConstraintViolation> violations;
  private final transient List<Class<?>> groups;

  /** Create with the given constraint violations */
  public ConstraintViolationException(String message, Set<ConstraintViolation> violations, List<Class<?>> groups) {
    super(message);
    this.violations = violations;
    this.groups = groups;
  }

  /** Return the constraint violations. */
  public Set<ConstraintViolation> violations() {
    return violations;
  }

  /** Return the groups used for validations. */
  public List<Class<?>> groups() {
    return groups;
  }
}
