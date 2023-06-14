package io.avaje.validation;

import io.avaje.validation.adapter.*;
import io.avaje.validation.core.DefaultBootstrap;
import io.avaje.validation.spi.Bootstrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

public interface Validator {

  /** Validate the object using the default locale. */
  void validate(Object any) throws ConstraintViolationException;

  /**
   * Validate the object with a given locale.
   *
   * <p>If the locale is not one of the supported locales then the default locale will be used.
   *
   * <p>This is expected to be used when the Validator is configured to support multiple locales.
   */
  void validate(Object any, Locale locale) throws ConstraintViolationException;

  static Builder builder() {
    final Iterator<Bootstrap> bootstrapService = ServiceLoader.load(Bootstrap.class).iterator();
    if (bootstrapService.hasNext()) {
      return bootstrapService.next().builder();
    }
    return DefaultBootstrap.builder();
  }

  /** Build the Validator instance adding ValidationAdapter, Factory or AdapterBuilder. */
  interface Builder {

    /** Add a ValidationAdapter to use for the given type. */
    <T> Builder add(Type type, ValidationAdapter<T> adapter);

    /** Add a AnnotationValidationAdapter to use for the given type. */
    <T> Builder add(Class<? extends Annotation> type, ValidationAdapter<T> adapter);

    /**
     * Lookup ResourceBundles with the given name and for error message interpolation
     *
     * @param bundleName the name of the bundleFiles
     */
    Builder addResourceBundles(String... bundleName);

    /**
     * Add ResourceBundle for error message interpolation
     *
     * @param bundleName the name of the bundleFiles
     */
    Builder addResourceBundles(ResourceBundle... bundle);

    /** Set Default Locale for this validator, if not set, will use Locale.getDefault() */
    Builder setDefaultLocale(Locale defaultLocale);

    /** Adds an additional Locales for this validator */
    Builder addLocales(Locale... locales);

    /** Add a AdapterBuilder which provides a ValidationAdapter to use for the given type. */
    Builder add(Type type, AdapterBuilder builder);

    /** Add a AdapterBuilder which provides a ValidationAdapter to use for the given type. */
    Builder add(Class<? extends Annotation> type, AnnotationAdapterBuilder builder);

    /** Add a Component which can provide multiple ValidationAdapters and or configuration. */
    Builder add(ValidatorComponent component);

    /** Add a ValidationAdapter.Factory which provides ValidationAdapters to use. */
    Builder add(ValidationContext.AdapterFactory factory);

    /** Add a ValidationAdapter.Factory which provides ValidationAdapters to use. */
    Builder add(ValidationContext.AnnotationFactory factory);

    /**
     * Build and return the Validator instance with all the given adapters and factories registered.
     */
    Validator build();
  }

  /** Function to build a ValidationAdapter that needs Validator. */
  @FunctionalInterface
  interface AdapterBuilder {

    /** Create a ValidationAdapter given the Validator instance. */
    ValidationAdapter<?> build(ValidationContext ctx);
  }

  /** Function to build a ValidationAdapter that needs Validator. */
  @FunctionalInterface
  interface AnnotationAdapterBuilder {

    /** Create a ValidationAdapter given the Validator instance. */
    ValidationAdapter<?> build(ValidationContext ctx, Map<String, Object> attributes);
  }

  /** Components register ValidationAdapters Validator.Builder */
  @FunctionalInterface
  interface GeneratedComponent extends ValidatorComponent {

    /** Register ValidationAdapters with the Builder. */
    @Override
    void register(Builder builder);
  }
}
