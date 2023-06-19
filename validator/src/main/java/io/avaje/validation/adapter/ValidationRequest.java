package io.avaje.validation.adapter;

/**
 * A validation request.
 */
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

  /**
   * Push the nested path.
   */
  void pushPath(String path);

  /**
   * Pop the nested path.
   */
  void popPath();

  /**
   * Throw ConstraintViolationException if there are violations for this request.
   */
  void throwWithViolations();

}
