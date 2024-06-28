package io.avaje.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks this Class as a MixIn Type that can add/modify constraint annotations on the specified type.
 * <p>
 * These types are typically in an external project / dependency or otherwise
 * types that we can't explicitly annotate or modify.
 * <p>
 * In the example below, the VehicleMixin class augments the the generated Vehicle
 * adapter to add a @NotBlank annotation to the type property.
 *
 * <pre>{@code
 *
 *   @MixIn(Vehicle.class)
 *   public abstract class VehicleMixIn {
 *
 *   @NotBlank
 *   private String type;
 *    ...
 *
 * }</pre>
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface MixIn {
  /** The concrete type to mix. */
  Class<?> value();
}
