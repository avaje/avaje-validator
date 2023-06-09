package io.avaje.validation.adapter;

import java.util.Objects;
import java.util.Set;
/**
 * This interface defines a set of validation methods for validating a value based on specific
 * rules. The methods in this interface allow for executing validations, composing validation
 * adapters, and checking validation groups.
 *
 * @param <T> The type of value to be validated
 */
public interface ValidationAdapter<T> {

  /**
   * Execute validations for the given value.
   *
   * @param value The value to be validated
   * @param req The validation request containing group/locale/violation information
   * @param propertyName The name of the property being validated
   * @return {@code true} if validation should continue, {@code false} otherwise
   */
  boolean validate(T value, ValidationRequest req, String propertyName);

  /**
   * Execute validations for the given value
   *
   * @param value The value to be validated
   * @param req The validation request containing group/locale/violation information
   * @return {@code true} if validation should continue, {@code false} otherwise
   */
  default boolean validate(T value, ValidationRequest req) {
    return validate(value, req, null);
  }

  /**
   * Create an adapter for validating a list of values.
   *
   * @return The adapter for list validation
   */
  default AbstractContainerAdapter<T> list() {
    return new IterableValidationAdapter<>(this);
  }

  /**
   * Create an adapter for validating map keys.
   *
   * @return The adapter for map key validation
   */
  default AbstractContainerAdapter<T> mapKeys() {
    return new MapValidationAdapter<>(this, true);
  }

  /**
   * Create an adapter for validating map values.
   *
   * @return The adapter for map value validation
   */
  default AbstractContainerAdapter<T> mapValues() {
    return new MapValidationAdapter<>(this, false);
  }

  /**
   * Create an adapter for validating an array.
   *
   * @return The adapter for array validation
   */
  default AbstractContainerAdapter<T> array() {
    return new ArrayValidationAdapter<>(this);
  }

  /**
   * Create an adapter for validating an optional value.
   *
   * @return The adapter for optional value validation
   */
  default AbstractContainerAdapter<T> optional() {
    return new OptionalValidationAdapter<>(this);
  }

  /**
   * Compose this validation adapter with another adapter by applying the validations in sequence.
   *
   * @param after The validation adapter to be applied after this adapter
   * @return The composed validation adapter
   * @throws NullPointerException if {@code after} is null
   */
  default ValidationAdapter<T> andThen(ValidationAdapter<? super T> after) {
    Objects.requireNonNull(after, "after cannot be null");
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        return after.validate(value, req, propertyName);
      }
      return true;
    };
  }

  /**
   * Check if the validation request groups are empty or match any of the adapter's configured
   * groups.
   *
   * @param adapterGroups The groups configured for this adapter
   * @param request The validation request containing the groups to be checked
   * @return {@code true} if the groups match or if the validation request groups are empty, {@code
   *     false} otherwise
   */
  default boolean checkGroups(Set<Class<?>> adapterGroups, ValidationRequest request) {
    final var requestGroups = request.groups();

    if (requestGroups.isEmpty()) return true;

    for (final var group : requestGroups) {
      if (adapterGroups.contains(group)) {
        return true;
      }
    }
    return false;
  }
}
