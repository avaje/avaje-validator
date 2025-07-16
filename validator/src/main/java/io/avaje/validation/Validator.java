package io.avaje.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;
import io.avaje.validation.core.DefaultBootstrap;
import io.avaje.validation.spi.AdapterFactory;
import io.avaje.validation.spi.AnnotationFactory;
import io.avaje.validation.spi.MessageInterpolator;
import io.avaje.validation.spi.ValidatorCustomizer;

/**
 * Validate plain Java objects that have been annotated with validation constraints.
 *
 * <pre>{@code
 *
 *   // create a validator
 *   final Validator validator = Validator.builder()
 *     .setDefaultLocale(Locale.CANADA)
 *     .addLocales(Locale.GERMAN)
 *     .build();
 *
 *  // validate a pojo
 *  Customer customer = ...;
 *  validator.validate(customer);
 *
 * }</pre>
 */
public interface Validator {

  /**
   * Validate the object using the default locale throwing ConstraintViolationException when
   * there are constraint violations.
   *
   * @param any The object to validate
   * @param groups The groups targeted for validation
   *
   * @throws ConstraintViolationException when there are constraint violations
   */
  void validate(Object any, @Nullable Class<?>... groups) throws ConstraintViolationException;

  /**
   * Validate the object with a given locale throwing ConstraintViolationException when
   * there are constraint violations.
   *
   * <p>If the locale is not one of the supported locales then the default locale will be used.
   *
   * <p>This is expected to be used when the Validator is configured to support multiple locales.

   * @param any The object to validate
   * @param locale The locale to use for constraint messages
   * @param groups The groups targeted for validation
   *
   * @throws ConstraintViolationException when there are constraint violations
   */
  void validate(Object any, @Nullable Locale locale, @Nullable Class<?>... groups)
      throws ConstraintViolationException;

  /**
   * Validate the object returning the constraint violations.
   *
   * @param any The object to validate
   * @param groups The groups targeted for validation
   *
   * @return The constraint violations
   */
  Set<ConstraintViolation> check(Object any, @Nullable Class<?>... groups);

  /**
   * Validate the object returning the constraint violations.
   *
   * @param any The object to validate
   * @param locale The locale to use for constraint messages
   * @param groups The groups targeted for validation
   *
   * @return The constraint violations
   */
  Set<ConstraintViolation> check(Object any, @Nullable Locale locale, @Nullable Class<?>... groups);

  /** Return the validation context used to create adapters */
  ValidationContext context();

  /**
   * Return the Builder used to build the Validator.
   *
   * <pre>{@code
   *
   *   final Validator validator = Validator.builder()
   *     .setDefaultLocale(Locale.CANADA)
   *     .addLocales(Locale.GERMAN)
   *     .build();
   *
   * }</pre>
   */
  static Builder builder() {
    return DefaultBootstrap.builder();
  }

  /**
   * Get the default Validator instance with all generated adapters configured.
   *
   * <p>This is a faster alternative to {@code Validator.builder().build()} that will return the
   * same singleton instance.
   */
  static Validator instance() {
    return DefaultBootstrap.instance();
  }

  /** Build the Validator instance adding ValidationAdapter, Factory or AdapterBuilder. */
  interface Builder {

    /** Add a ValidationAdapter to use for the given type. */
    <T> Builder add(Type type, ValidationAdapter<T> adapter);

    /** Add a AnnotationValidationAdapter to use for the given type. */
    <T> Builder add(Class<? extends Annotation> type, ValidationAdapter<T> adapter);

    /**
     * Lookup ResourceBundles with the given names for error message interpolation. This will
     * attempt to load the bundles for every locale configured with this builder
     */
    Builder addResourceBundles(String... bundleName);

    /** Add ResourceBundles for error message interpolation */
    Builder addResourceBundles(ResourceBundle... bundle);

    /** Set Default Locale for this validator. If not set, will use Locale.getDefault() */
    Builder setDefaultLocale(Locale defaultLocale);

    /** Adds additional Locales for this validator */
    Builder addLocales(Locale... locales);

    /** Set the ClassLoader to use when loading adapters. */
    Builder classLoader(ClassLoader classLoader);

    /**
     * Contract for obtaining the Clock used as the reference for now when validating the
     * {@code @Future} and {@code @Past} constraints.
     */
    Builder clockProvider(Supplier<Clock> clockSupplier);

    /** Define the acceptable margin of error when comparing date/time in temporal constraints. */
    Builder temporalTolerance(Duration temporalTolerance);

    /**
    * Set the MessageInterpolator that will be used to parse and interpolate error messages
    */
    Builder messageInterpolator(MessageInterpolator interpolator);

    /**
     * Enable/Disable fail fast mode. When fail fast is enabled the validation will stop on the
     * first constraint violation detected.
     */
    Builder failFast(boolean failFast);

    /** Add a AdapterBuilder which provides a ValidationAdapter to use for the given type. */
    Builder add(Type type, AdapterBuilder builder);

    /**
     * Add a AdapterBuilder which provides a Annotation ValidationAdapter to use for the given type.
     */
    Builder add(Class<? extends Annotation> type, AnnotationAdapterBuilder builder);

    /** Add a Component which can provide multiple ValidationAdapters and or configuration. */
    Builder add(ValidatorCustomizer component);

    /** Add a ValidationAdapter.Factory which provides ValidationAdapters to use. */
    Builder add(AdapterFactory factory);

    /** Add a ValidationAdapter.Factory which provides ValidationAdapters to use. */
    Builder add(AnnotationFactory factory);

    /**
     * Build and return the Validator instance with all the given adapters and factories registered.
     */
    Validator build();
  }

  /** Function to build a ValidationAdapter from a Validation Context */
  @FunctionalInterface
  interface AdapterBuilder {

    /** Create a ValidationAdapter given the Validator instance. */
    ValidationAdapter<?> build(ValidationContext ctx);
  }

  /** Function to build a ValidationAdapter that needs Validator. */
  @FunctionalInterface
  interface AnnotationAdapterBuilder {

    /** Create a ValidationAdapter given the Validator instance. */
    ValidationAdapter<?> build(AdapterCreateRequest request);
  }
}
