package io.avaje.validation.core;

import static io.avaje.validation.core.Util.canonicalize;
import static io.avaje.validation.core.Util.canonicalizeClass;
import static io.avaje.validation.core.Util.removeSubtypeWildcard;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import io.avaje.lang.Nullable;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;
import io.avaje.validation.spi.MessageInterpolator;
import io.avaje.validation.spi.ValidatorCustomizer;

/** Default implementation of Validator. */
final class DValidator implements Validator, ValidationContext {

  private final CoreAdapterBuilder builder;
  private final Map<Type, ValidationType<?>> typeCache = new ConcurrentHashMap<>();
  private final MessageInterpolator interpolator;
  private final LocaleResolver localeResolver;
  private final TemplateLookup templateLookup;
  private final Map<String, String> messageCache = new ConcurrentHashMap<>();
  private final boolean failfast;

  DValidator(
      List<AdapterFactory> factories,
      List<AnnotationFactory> annotationFactories,
      List<String> bundleNames,
      List<ResourceBundle> bundles,
      MessageInterpolator interpolator,
      LocaleResolver localeResolver,
      Supplier<Clock> clockSupplier,
      Duration temporalTolerance,
      boolean failfast) {
    this.localeResolver = localeResolver;
    final var defaultResourceBundle =
        new ResourceBundleManager(bundleNames, bundles, localeResolver);
    this.templateLookup = new TemplateLookup(defaultResourceBundle);
    this.interpolator = interpolator;
    this.builder =
        new CoreAdapterBuilder(
            this, factories, annotationFactories, clockSupplier, temporalTolerance);
    this.failfast = failfast;
  }

  MessageInterpolator interpolator() {
    return this.interpolator;
  }

  @Override
  public void validate(Object any, @Nullable Class<?>... groups) {
    validate(any, null, groups);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void validate(Object any, @Nullable Locale locale, @Nullable Class<?>... groups) {
    final var type = (ValidationType<Object>) type(any.getClass());
    type.validate(any, locale, List.of(groups));
  }

  @Override
  public Set<ConstraintViolation> check(Object any, @Nullable Class<?>... groups) {
    return check(any, null, groups);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<ConstraintViolation> check(Object any, @Nullable Locale locale, @Nullable Class<?>... groups) {
    final var type = (ValidationType<Object>) type(any.getClass());
    return type.check(any, locale, List.of(groups));
  }

  @Override
  public ValidationContext context() {
    return this;
  }

  private <T> ValidationType<T> type(Class<T> cls) {
    return typeWithCache(cls);
  }

  @SuppressWarnings("unchecked")
  private <T> ValidationType<T> typeWithCache(Type type) {
    return (ValidationType<T>)
        typeCache.computeIfAbsent(type, k -> new ValidationType<>(this, adapter(k)));
  }

  @Override
  public Message message(Map<String, Object> attributes) {
    final String keyOrTemplate = (String) attributes.get("message");
    // if configured to support only 1 Locale then we can do the lookup and message translation once
    // and early otherwise we defer as the final message is locale specific
    return new DMessage(keyOrTemplate, attributes);
  }

  @Override
  public Message message(String message, Map<String, Object> attributes) {
    return new DMessage(message, attributes);
  }

  @Override
  public <T> ValidationAdapter<T> adapter(Class<T> cls) {
    final Type cacheKey = canonicalizeClass(requireNonNull(cls));
    final ValidationAdapter<T> result = builder.get(cacheKey);
    if (result != null) {
      return result;
    }
    return builder.build(cacheKey);
  }

  @Override
  public <T> ValidationAdapter<T> adapter(
      Class<? extends Annotation> cls, Map<String, Object> attributes) {
    return builder.annotationAdapter(cls, attributes, null);
  }

  @Override
  public <T> ValidationAdapter<T> adapter(
      Class<? extends Annotation> cls,
      Set<Class<?>> groups,
      String message,
      Map<String, Object> attributes) {
    attributes = new HashMap<>(attributes);
    attributes.put("message", message);
    return builder.annotationAdapter(cls, Map.copyOf(attributes), groups);
  }

  @Override
  public <T> ValidationAdapter<T> adapter(Type type) {
    type = removeSubtypeWildcard(canonicalize(requireNonNull(type)));
    final Object cacheKey = type;
    final ValidationAdapter<T> result = builder.get(cacheKey);
    if (result != null) {
      return result;
    }
    return builder.build(type, cacheKey);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> ValidationAdapter<T> noop() {
    return CoreAdapterBuilder.NOOP;
  }

  @Override
  public ValidationRequest request(@Nullable Locale locale, List<Class<?>> groups) {
    return new DRequest(this, failfast, locale, groups);
  }

  String interpolate(Message msg, Locale requestLocale) {
    // resolve the locale to use to produce the message
    final Locale locale = localeResolver.resolve(requestLocale);

    return messageCache.computeIfAbsent(
        msg.lookupkey() + locale,
        k -> {
          // lookup in resource bundles using resolved locale and template
          final String template = templateLookup.lookup(msg.template(), locale);
          return interpolator.interpolate(template, msg.attributes());
        });
  }

  /** Implementation of Validator.Builder. */
  static final class DBuilder implements Validator.Builder {

    private final List<AdapterFactory> factories = new ArrayList<>();
    private final List<AnnotationFactory> afactories = new ArrayList<>();
    private final List<String> bundleNames = new ArrayList<>();
    private final List<ResourceBundle> bundles = new ArrayList<>();
    private final List<Locale> otherLocales = new ArrayList<>();
    private Locale defaultLocale = Locale.getDefault();
    private Supplier<Clock> clockSupplier = Clock::systemDefaultZone;
    private Duration temporalTolerance = Duration.ZERO;
    private boolean failfast;
    private MessageInterpolator userInterpolator;

    @Override
    public Builder add(Type type, AdapterBuilder builder) {
      return add(newAdapterFactory(type, builder));
    }

    @Override
    public Builder add(Class<? extends Annotation> type, AnnotationAdapterBuilder builder) {
      return add(newAdapterFactory(type, builder));
    }

    @Override
    public <T> Builder add(Type type, ValidationAdapter<T> adapter) {
      return add(newAdapterFactory(type, adapter));
    }

    @Override
    public Builder add(ValidatorCustomizer component) {
      component.customize(this);
      return this;
    }

    @Override
    public Builder add(AdapterFactory factory) {
      factories.add(factory);
      return this;
    }

    @Override
    public <T> Builder add(Class<? extends Annotation> type, ValidationAdapter<T> adapter) {
      return add(newAnnotationAdapterFactory(type, adapter));
    }

    @Override
    public Builder add(AnnotationFactory factory) {
      afactories.add(factory);
      return this;
    }

    @Override
    public Builder addResourceBundles(String... bundleName) {
      Collections.addAll(bundleNames, bundleName);
      return this;
    }

    @Override
    public Builder addResourceBundles(ResourceBundle... bundle) {
      Collections.addAll(bundles, bundle);
      return this;
    }

    @Override
    public Builder setDefaultLocale(Locale defaultLocal) {
      this.defaultLocale = defaultLocal;
      return this;
    }

    @Override
    public Builder addLocales(Locale... locals) {
      Collections.addAll(otherLocales, locals);
      return this;
    }

    @Override
    public Builder clockProvider(Supplier<Clock> clockSupplier) {
      this.clockSupplier = clockSupplier;
      return this;
    }

    @Override
    public Builder temporalTolerance(Duration temporalTolerance) {
      this.temporalTolerance = temporalTolerance;
      return this;
    }

    @Override
    public Builder failFast(boolean failfast) {
      this.failfast = failfast;
      return this;
    }

    @Override
    public Builder messageInterpolator(MessageInterpolator interpolator) {
      this.userInterpolator = interpolator;
      return this;
    }

    private void registerComponents() {
      // first register all user defined ValidatorCustomizer
      for (final ValidatorCustomizer next : ServiceLoader.load(ValidatorCustomizer.class)) {
        next.customize(this);
      }
      for (final GeneratedComponent next : ServiceLoader.load(GeneratedComponent.class)) {
        next.customize(this);
      }
    }

    @Override
    public DValidator build() {
      registerComponents();

      final var localeResolver = new LocaleResolver(defaultLocale, otherLocales);
      final var interpolator =
          Optional.ofNullable(this.userInterpolator)
              .or(() -> ServiceLoader.load(MessageInterpolator.class).findFirst())
              .orElseGet(BasicMessageInterpolator::new);

      return new DValidator(
          factories,
          afactories,
          bundleNames,
          bundles,
          interpolator,
          localeResolver,
          clockSupplier,
          temporalTolerance,
          failfast);
    }

    private static <T> AnnotationFactory newAnnotationAdapterFactory(
        Type type, ValidationAdapter<T> adapter) {
      requireNonNull(type);
      requireNonNull(adapter);
      return request -> simpleMatch(type, request.annotationType()) ? adapter : null;
    }

    private static <T> AdapterFactory newAdapterFactory(Type type, ValidationAdapter<T> adapter) {
      requireNonNull(type);
      requireNonNull(adapter);
      return (targetType, context) -> simpleMatch(type, targetType) ? adapter : null;
    }

    private static AdapterFactory newAdapterFactory(Type type, AdapterBuilder builder) {
      requireNonNull(type);
      requireNonNull(builder);
      return (targetType, ctx) -> simpleMatch(type, targetType) ? builder.build(ctx) : null;
    }

    private static AnnotationFactory newAdapterFactory(
        Class<? extends Annotation> type, AnnotationAdapterBuilder builder) {
      requireNonNull(type);
      requireNonNull(builder);
      return req -> simpleMatch(type, req.annotationType()) ? builder.build(req) : null;
    }

    private static boolean simpleMatch(Type type, Type targetType) {
      return Util.typesMatch(type, targetType);
    }
  }
}
