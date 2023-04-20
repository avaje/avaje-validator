package io.avaje.validation;

/**
 * Describes a constraint violation. This object exposes the constraint violation context as well as
 * the message describing the violation.
 */
public class ConstraintViolation {

  private final String path;
  private final String message;

  /**
   * TODO: REMOVE this constructor
   */
  public ConstraintViolation(String message) {
    this.path = null;
    this.message = message;
  }

  public ConstraintViolation(String path, String message) {
    this.path = path;
    this.message = message;
  }

  /** Return the path that this violation occurred for */
  public String path() {
    return path;
  }

  /** Return the interpolated error message for this constraint violation */
  public String message() {
    return message;
  }
}
