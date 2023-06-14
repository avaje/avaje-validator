package io.avaje.validation.core;

import static io.avaje.validation.core.Util.canonicalize;
import static io.avaje.validation.core.Util.canonicalizeClass;
import static io.avaje.validation.core.Util.removeSubtypeWildcard;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import io.avaje.lang.Nullable;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;
import io.avaje.validation.adapter.ValidatorComponent;

/** Default implementation of Validator. */
final class DValidator implements Validator, ValidationContext {

  private final CoreAdapterBuilder builder;
  private final Map<Type, DValidationType<?>> typeCache = new ConcurrentHashMap<>();
  private final MessageInterpolator interpolator;
  private final LocaleResolver localeResolver;
  private final DTemplateLookup templateLookup;
  private final Map<String, String> messageCache = new HashMap<>();

  DValidator(
      List<AdapterFactory> factories,
      List<AnnotationFactory> annotationFactories,
      List<String> bundleNames,
      List<ResourceBundle> bundles,
      MessageInterpolator interpolator,
      LocaleResolver localeResolver) {
    this.localeResolver = localeResolver;
    final var defaultResourceBundle = new DResourceBundleManager(bundleNames, bundles, localeResolver);
    this.templateLookup = new DTemplateLookup(defaultResourceBundle);
    this.interpolator = interpolator;
    this.builder = new CoreAdapterBuilder(this, factories, annotationFactories);
  }

  MessageInterpolator interpolator() {
    return this.interpolator;
  }

  @Override
  public void validate(Object any) {
    validate(any, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void validate(Object any, @Nullable Locale locale) {
    final var type = (ValidationType<Object>) type(any.getClass());
    type.validate(any, locale);
  }

  private <T> ValidationType<T> type(Class<T> cls) {
    return typeWithCache(cls);
  }

  @SuppressWarnings("unchecked")
  private <T> ValidationType<T> typeWithCache(Type type) {
    return (ValidationType<T>) typeCache.computeIfAbsent(type, _type -> new DValidationType<>(this, adapter(_type)));
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
  public <T> ValidationAdapter<T> adapter(Class<? extends Annotation> cls, Map<String, Object> attributes) {
    return builder.annotationAdapter(cls, attributes);
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

  ValidationRequest request(@Nullable Locale locale) {
    return new DRequest(this, locale);
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
    private final List<Locale> otherLocals = new ArrayList<>();
    private Locale defaultLocal = Locale.getDefault();

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
    public Builder add(ValidatorComponent component) {
      component.register(this);
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
      this.defaultLocal = defaultLocal;
      return this;
    }

    @Override
    public Builder addLocales(Locale... locals) {
      Collections.addAll(otherLocals, locals);
      return this;
    }

    private void registerComponents() {
      // first register all user defined ValidatorComponent
      for (final ValidatorComponent next : ServiceLoader.load(ValidatorComponent.class)) {
        next.register(this);
      }
      for (final GeneratedComponent next : ServiceLoader.load(GeneratedComponent.class)) {
        next.register(this);
      }
    }

    @Override
    public DValidator build() {
      registerComponents();

      final LocaleResolver localeResolver = new DLocaleResolver(defaultLocal, otherLocals);
      final var interpolator =
          ServiceLoader.load(MessageInterpolator.class)
              .findFirst()
              .orElseGet(BasicMessageInterpolator::new);
      return new DValidator(factories, afactories, bundleNames, bundles, interpolator, localeResolver);
    }

    private static <T> AnnotationFactory newAnnotationAdapterFactory(Type type, ValidationAdapter<T> adapter) {
      requireNonNull(type);
      requireNonNull(adapter);
      return (targetType, context, attributes) -> simpleMatch(type, targetType) ? adapter : null;
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

    private static AnnotationFactory newAdapterFactory(Class<? extends Annotation> type, AnnotationAdapterBuilder builder) {
      requireNonNull(type);
      requireNonNull(builder);
      return (targetType, ctx, attributes) -> simpleMatch(type, targetType) ? builder.build(ctx, attributes) : null;
    }
  }

  private static boolean simpleMatch(Type type, Type targetType) {
    return Util.typesMatch(type, targetType);
  }
}
