package io.avaje.validation.adapter;

import java.util.Set;

/**
 * Abstract Adapter that validates objects based on Constraint Annotations.
 *
 */
public abstract class AbstractConstraintAdapter<T> implements ValidationAdapter<T> {

  protected final ValidationContext.Message message;
  protected final Set<Class<?>> groups;

  /** @param initialAdapter initial adapter that can be used to validate the container itself */
  protected AbstractConstraintAdapter(ValidationContext.Message message, Set<Class<?>> groups) {
    this.message = message;
    this.groups = groups;
  }

  protected abstract boolean isValid(T value);

  @Override
  public final boolean validate(T value, ValidationRequest req, String propertyName) {
    if (!checkGroups(groups, req)) {
      return true;
    }

    if (!isValid(value)) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }
}
