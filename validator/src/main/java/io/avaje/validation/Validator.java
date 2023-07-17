package io.avaje.validation;

import io.avaje.lang.Nullable;
import io.avaje.validation.adapter.*;
import io.avaje.validation.core.DefaultBootstrap;
import io.avaje.validation.spi.ValidatorCustomizer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Supplier;

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

  /** Validate the object using the default locale. */
  void validate(Object value, @Nullable Class<?>... groups) throws ConstraintViolationException;

  /**
   * Validate the object with a given locale.
   *
   * <p>If the locale is not one of the supported locales then the default locale will be used.
   *
   * <p>This is expected to be used when the Validator is configured to support multiple locales.
   */
  void validate(Object any, @Nullable Locale locale, @Nullable Class<?>... groups)
      throws ConstraintViolationException;

  /** Return the validation context used to create adapters */
  ValidationContext content();

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

  /** Build the Validator instance adding ValidationAdapter, Factory or AdapterBuilder. */
  interface Builder {

    /** Add a ValidationAdapter to use for the given type. */
    <T> Builder add(Type type, ValidationAdapter<T> adapter);

    /** Add a AnnotationValidationAdapter to use for the given type. */
    <T> Builder add(Class<? extends Annotation> type, ValidationAdapter<T> adapter);

    /** Lookup ResourceBundles with the given names for error message interpolation */
    Builder addResourceBundles(String... bundleName);

    /** Add ResourceBundles for error message interpolation */
    Builder addResourceBundles(ResourceBundle... bundle);

    /** Set Default Locale for this validator. If not set, will use Locale.getDefault() */
    Builder setDefaultLocale(Locale defaultLocale);

    /** Adds additional Locales for this validator */
    Builder addLocales(Locale... locales);

    /**
     * Contract for obtaining the Clock used as the reference for now when validating the @Future
     * and @Past constraints.
     */
    Builder clockProvider(Supplier<Clock> clockSupplier);

    /** Define the acceptable margin of error when comparing date/time in temporal constraints. */
    Builder temporalTolerance(Duration temporalTolerance);

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
    Builder add(ValidationContext.AdapterFactory factory);

    /** Add a ValidationAdapter.Factory which provides ValidationAdapters to use. */
    Builder add(ValidationContext.AnnotationFactory factory);

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
    ValidationAdapter<?> build(
        ValidationContext ctx, Set<Class<?>> groups, Map<String, Object> attributes);
  }

  /** Components register ValidationAdapters Validator.Builder */
  @FunctionalInterface
  interface GeneratedComponent extends ValidatorCustomizer {

    /** Customize the Builder with generated ValidationAdapters. */
    @Override
    void customize(Builder builder);
  }
}
