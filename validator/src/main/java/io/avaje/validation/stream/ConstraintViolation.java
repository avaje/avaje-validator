package io.avaje.validation.stream;

/**
 * Describes a constraint violation. This object exposes the constraint violation context as well as
 * the message describing the violation.
 *
 * @param <T> the type of the root bean
 * @author Emmanuel Bernard
 */
public class ConstraintViolation {

  private final String message;

  public ConstraintViolation(String message) {
    this.message = message;
  }

  /** @return the interpolated error message for this constraint violation */
  public String message() {
    return message;
  }
}
