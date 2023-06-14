package io.avaje.validation;

/**
 * Describes a constraint violation. This object exposes the constraint violation context as well as
 * the message describing the violation.
 */
public class ConstraintViolation {

  private final String path;
  private final String propertyName;
  private final String message;

  /**
   * Create the ConstraintViolation with the given path, property and message.
   */
  public ConstraintViolation(String path, String propertyName, String message) {
    this.path = path;
    this.propertyName = propertyName;
    this.message = message;
  }

  /** Return the path for this constraint violation */
  public String path() {
    return path;
  }

  /** Return the property name for this constraint violation */
  public String propertyName() {
    return propertyName;
  }

  /** Return the interpolated error message for this constraint violation */
  public String message() {
    return message;
  }

  @Override
  public String toString() {
    return message;
  }
}
