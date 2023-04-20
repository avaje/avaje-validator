package io.avaje.validation.core;

import static io.avaje.validation.core.Util.canonicalize;
import static io.avaje.validation.core.Util.canonicalizeClass;
import static io.avaje.validation.core.Util.removeSubtypeWildcard;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.avaje.validation.ValidationAdapter;
import io.avaje.validation.ValidationType;
import io.avaje.validation.Validator;
import io.avaje.validation.ValidatorComponent;
import io.avaje.validation.core.AnnotationValidationAdapter.Factory;
import io.avaje.validation.ConstraintViolation;

/** Default implementation of Validator. */
final class DValidator implements Validator {

  private final CoreAdapterBuilder builder;
  private final Map<Type, DValidationType<?>> typeCache = new ConcurrentHashMap<>();
  private final MessageInterpolator interpolator;

  DValidator(
      List<ValidationAdapter.Factory> factories,
      List<AnnotationValidationAdapter.Factory> annotationFactories,
      MessageInterpolator interpolator) {
    this.interpolator = interpolator;
    this.builder = new CoreAdapterBuilder(this, factories, annotationFactories);
  }

  public MessageInterpolator interpolator() {
    return this.interpolator;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<ConstraintViolation> validate(Object any) {
    final var type = (ValidationType<Object>) type(any.getClass());
    return type.validate(any);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<ConstraintViolation> validate(Collection<Object> any) {
    final var violations = new HashSet<ConstraintViolation>();
    for (final Object object : any) {
      final var type = (ValidationType<Object>) type(object.getClass());
      type.validate(any, violations);
    }

    return violations;
  }

  private <T> ValidationType<T> type(Class<T> cls) {
    return typeWithCache(cls);
  }

  @SuppressWarnings("unchecked")
  private <T> ValidationType<T> typeWithCache(Type type) {
    return (ValidationType<T>)
        typeCache.computeIfAbsent(
            type, _type -> new DValidationType<>(this, _type, adapter(_type)));
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
  public <T> AnnotationValidationAdapter<T> annotationAdapter(Class<? extends Annotation> cls) {
    final AnnotationValidationAdapter<T> result = builder.annotationAdapter(cls);
    if (result != null) {
      return result;
    }
    return builder.annotationAdapter(cls);
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

  /** Implementation of Validator.Builder. */
  static final class DBuilder implements Validator.Builder {

    private final List<ValidationAdapter.Factory> factories = new ArrayList<>();
    private final List<AnnotationValidationAdapter.Factory> afactories = new ArrayList<>();

    @Override
    public Builder add(Type type, AdapterBuilder builder) {
      return add(newAdapterFactory(type, builder));
    }

    @Override
    public <T> Builder add(Type type, ValidationAdapter<T> jsonAdapter) {
      return add(newAdapterFactory(type, jsonAdapter));
    }

    @Override
    public Builder add(ValidatorComponent component) {
      component.register(this);
      return this;
    }

    @Override
    public Builder add(ValidationAdapter.Factory factory) {
      factories.add(factory);
      return this;
    }

    @Override
    public <T> Builder add(Class<Annotation> type, AnnotationValidationAdapter<T> jsonAdapter) {
      return add(newAnnotationAdapterFactory(type, jsonAdapter));
    }

    @Override
    public Builder add(Factory factory) {
      afactories.add(factory);
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

      final var interpolator =
          ServiceLoader.load(MessageInterpolator.class)
              .findFirst()
              .orElseGet(NooPMessageInterpolator::new);
      return new DValidator(factories, afactories, interpolator);
    }

    static <T> AnnotationValidationAdapter.Factory newAnnotationAdapterFactory(
        Type type, AnnotationValidationAdapter<T> jsonAdapter) {
      requireNonNull(type);
      requireNonNull(jsonAdapter);
      return (targetType, jsonb, i) -> simpleMatch(type, targetType) ? jsonAdapter : null;
    }

    static <T> ValidationAdapter.Factory newAdapterFactory(
        Type type, ValidationAdapter<T> jsonAdapter) {
      requireNonNull(type);
      requireNonNull(jsonAdapter);
      return (targetType, jsonb) -> simpleMatch(type, targetType) ? jsonAdapter : null;
    }

    static <T> ValidationAdapter.Factory newAdapterFactory(Type type, AdapterBuilder builder) {
      requireNonNull(type);
      requireNonNull(builder);
      return (targetType, jsonb) -> simpleMatch(type, targetType) ? builder.build(jsonb) : null;
    }
  }

  private static boolean simpleMatch(Type type, Type targetType) {
    return Util.typesMatch(type, targetType);
  }
}
