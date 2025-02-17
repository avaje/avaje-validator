package io.avaje.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specify the subtypes that a given type can be represented as.
 * <p>
 * This is used on an interface type, abstract type or type with inheritance
 * to indicate all the concrete subtypes that can represent the type.
 * <p>
 * In the example below the abstract Vehicle type has 2 concrete subtypes
 * of Car and Truck that can represent the type.
 *
 * <pre>{@code
 *
 *   @ValidSubTypes(Car.class, Truck.class)
 *   public abstract class Vehicle {
 *    ...
 *
 * }</pre>
 *
 *  * <p>
 * In the example below the abstract Vehicle type has 2 concrete subtypes
 * of Car and Truck that can represent the type.
 *
 * <pre>{@code
 *
 *   @ValidSubTypes(Car.class, Truck.class)
 *   public abstract class Vehicle {
 *    ...
 *
 * }</pre>
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface ValidSubTypes {

  /** Subclasses of the current type. Not needed for sealed classes */
  Class<?>[] value() default {};
}
