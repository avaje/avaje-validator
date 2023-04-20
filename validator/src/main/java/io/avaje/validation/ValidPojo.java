package io.avaje.validation;

import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a type for JSON support.
 *
 * <h3>Examples:</h3>
 *
 * <pre>{@code
 *
 *   @ValidPojo(naming = LowerHyphen)
 *   public class Customer ...
 *
 * }</pre>
 *
 * <pre>{@code
 *
 *   @ValidPojo
 *   public record Product( ... )
 *
 * }</pre>
 */
@Retention(CLASS)
@Target(ElementType.TYPE)
public @interface ValidPojo {

  /**
   * Specify types to generate JsonAdapters for.
   * <p>
   * These types are typically in an external project / dependency or otherwise
   * types that we can't or don't want to explicitly annotate with {@code @ValidPojo}.
   * <p>
   * Typically, we put this annotation on a package.
   *
   * <pre>{@code
   *
   *   @ValidPojo.Import({Customer.class, Product.class, ...})
   *   package org.example.processor;
   *
   * }</pre>
   */
  @Retention(CLASS)
  @Target({ElementType.TYPE, ElementType.PACKAGE})
  @interface Import {

    /**
     * Specify types to generate ValidPojo Adapters for.
     */
    Class<?>[] value();
  }
}
