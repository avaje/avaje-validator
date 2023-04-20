package io.avaje.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import io.avaje.validation.core.AnnotationValidationAdapter;
import io.avaje.validation.core.DefaultBootstrap;
import io.avaje.validation.spi.Bootstrap;
import io.avaje.validation.stream.ConstraintViolation;

/**
 * Provides access to json adapters by type.
 *
 * <h4>Initialise with defaults</h3>
 *
 * <pre>{@code
 * Validator jsonb = Validator.builder().build();
 * }</pre>
 *
 * <h4>Initialise with some configuration</h3>
 *
 * <pre>{@code
 * Validator jsonb = Validator.builder()
 *   .serializeNulls(true)
 *   .serializeEmpty(true)
 *   .failOnUnknown(true)
 *   .build();
 * }</pre>
 *
 * <h4>Initialise using Jackson core with configuration</h3>
 *
 * <p>We need to include the dependency <code>io.avaje:avaje-jsonb-jackson</code> to do this. This
 * will use Jackson core BaseValidator and JsonGenerator to do the underlying parsing and
 * generation.
 *
 * <pre>{@code
 * // create the Jackson JsonFactory
 * JsonFactory customFactory = ...;
 *
 * var jacksonAdapter = JacksonAdapter.builder()
 *   .serializeNulls(true)
 *   .jsonFactory(customFactory)
 *   .build();
 *
 * Validator jsonb = Validator.builder()
 *   .adapter(jacksonAdapter)
 *   .build();
 *
 * }</pre>
 *
 * <h4>fromJson</h4>
 *
 * <p>Read json content from: String, byte[], Reader, InputStream, JsonReader
 *
 * <pre>{@code
 * ValidationType<Customer> customerType = jsonb.type(Customer.class);
 *
 * Customer customer = customerType.fromJson(content);
 *
 * }</pre>
 *
 * <h4>toJson</h4>
 *
 * <p>Write json content to: String, byte[], Writer, OutputStream, JsonWriter
 *
 * <pre>{@code
 * ValidationType<Customer> customerType = jsonb.type(Customer.class);
 *
 * String asJson = customerType.toJson(customer);
 *
 * }</pre>
 */
public interface Validator {

  /**
   * Create a new Validator.Builder to configure and build the Validator instance.
   *
   * <p>We can register ValidationAdapter's to use for specific types before building and returning
   * the Validator instance to use.
   *
   * <p>Note that ValidationAdapter's that are generated are automatically registered via service
   * loading so there is no need to explicitly register those generated JsonAdapters.
   *
   * <pre>{@code
   * Validator jsonb = Validator.builder()
   *   .serializeNulls(true)
   *   .serializeEmpty(true)
   *   .failOnUnknown(true)
   *   .build();
   *
   * }</pre>
   */
  static Builder builder() {
    final Iterator<Bootstrap> bootstrapService = ServiceLoader.load(Bootstrap.class).iterator();
    if (bootstrapService.hasNext()) {
      return bootstrapService.next().builder();
    }
    return DefaultBootstrap.builder();
  }



  /**
   * Return json content for the given object.
   *
   * <p>This is a convenience method for {@code jsonb.type(Object.class).toJson(any) }
   *
   * @param any The object to return as json string
   * @return Return json content for the given object.
   */
  Set<ConstraintViolation> validate(Object any);


  /**
   * Return json content for the given object.
   *
   * <p>This is a convenience method for {@code jsonb.type(Object.class).toJson(any) }
   *
   * @param any The object to return as json string
   * @return Return json content for the given object.
   */
  Set<ConstraintViolation> validate(Collection<Object> any);

  /**
   * Return the ValidationAdapter used to read and write json for the given class.
   *
   * <p>ValidationAdapter is generally used by generated code and your application code is expected
   * to use {@link Validator#type(Class)} and {@link ValidationType} instead.
   */
  <T> ValidationAdapter<T> adapter(Class<T> cls);

  /**
   * Return the ValidationAdapter used to read and write json for the given type.
   *
   * <p>ValidationAdapter is generally used by generated code and your application code is expected
   * to use {@link Validator#type(Type)} and {@link ValidationType} instead.
   */
  <T> ValidationAdapter<T> adapter(Type type);

  <T> AnnotationValidationAdapter<T> annotationAdapter(Class<? extends Annotation> cls);

  /** Build the Validator instance adding ValidationAdapter, Factory or AdapterBuilder. */
  interface Builder {

    /** Add a ValidationAdapter to use for the given type. */
    <T> Builder add(Type type, ValidationAdapter<T> jsonAdapter);

    /** Add a AnnotationValidationAdapter to use for the given type. */
    <T> Builder add(Class<Annotation> type, AnnotationValidationAdapter<T> jsonAdapter);

    /** Add a AdapterBuilder which provides a ValidationAdapter to use for the given type. */
    Builder add(Type type, AdapterBuilder builder);

    /** Add a Component which can provide multiple JsonAdapters and or configuration. */
    Builder add(ValidatorComponent component);

    /** Add a ValidationAdapter.Factory which provides JsonAdapters to use. */
    Builder add(ValidationAdapter.Factory factory);

    /** Add a ValidationAdapter.Factory which provides JsonAdapters to use. */
    Builder add(AnnotationValidationAdapter.Factory factory);

    /**
     * Build and return the Validator instance with all the given adapters and factories registered.
     */
    Validator build();
  }

  /** Function to build a ValidationAdapter that needs Validator. */
  @FunctionalInterface
  interface AdapterBuilder {

    /** Create a ValidationAdapter given the Validator instance. */
    ValidationAdapter<?> build(Validator jsonb);
  }

  /** Components register JsonAdapters Validator.Builder */
  @FunctionalInterface
  interface GeneratedComponent extends ValidatorComponent {

    /** Register JsonAdapters with the Builder. */
    @Override
    void register(Builder builder);
  }
}
