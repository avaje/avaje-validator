/**
 * Avaje Validation API - see {@link io.avaje.validation.Validator}.
 *
 * <h2>Example:</h2>
 *
 * <pre>{@code
 * // Annotate classes with @Valid
 *
 * @Valid
 * public class Address {
 *
 *   // annotate fields with constraints
 *   @NotBlank
 *   private String street;
 *
 *   @NotEmpty(message="must not be empty")
 *   private List<@NotBlank String> owners; // message will be interpolated from bundle
 *
 *   //getters/setters
 * }
 *
 * --------------------------------------------------
 *
 * final Validator validator = Validator.builder().build();
 *
 * Address address = ...;
 * validator.validate(address);
 *
 * }</pre>
 */
module io.avaje.validation {
  exports io.avaje.validation;
  exports io.avaje.validation.adapter;
  exports io.avaje.validation.groups;
  exports io.avaje.validation.spi;

  requires io.avaje.applog;
  requires static io.avaje.inject;
  requires static io.avaje.inject.aop;
  requires static io.avaje.spi;
  requires static transitive org.jspecify;

  uses io.avaje.validation.spi.ValidationExtension;
}
