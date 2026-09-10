package io.avaje.validation;

import java.util.Set;

/**
 * Describes a single constraint violation that occurred while validating an object.
 *
 * @param path The full dotted path from the root validated object to the property that failed,
 *     including any container indices/keys for elements within a list, set, array or map (e.g.
 *     {@code "address.street"}, or {@code "contacts[1].email"}).
 * @param field The property name, or container element indicator (e.g. {@code "[1]"} for a list
 *     index or {@code "[key]"} for a map key), that failed.
 * @param message The interpolated, human-readable error message for this violation.
 * @param payload The {@code payload} classes declared on the violated constraint, or an empty set
 *     when none were declared.
 */
public record ConstraintViolation(
    String path, String field, String message, Set<Class<?>> payload) {

  /** Create a violation with no payload. */
  public ConstraintViolation(String path, String field, String message) {
    this(path, field, message, Set.of());
  }
}
