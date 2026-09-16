package io.avaje.validation.constraints;

/**
 * Defines a payload type that can be attached to a given constraint declaration.
 *
 * <p>Payloads are typically used to carry on metadata information consumed by a validation client.
 *
 * <p>An example would be to describe the severity of a constraint violation.
 *
 * <pre>{@code
 * @NotNull(payload = Severity.Error.class)
 * private String name;
 * }</pre>
 */
public interface Payload {}
