package io.avaje.validation.core;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import io.avaje.lang.Nullable;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.adapters.BasicAdapters;
import io.avaje.validation.core.adapters.FuturePastAdapterFactory;
import io.avaje.validation.core.adapters.NumberAdapters;

/** Builds and caches the ValidationAdapter adapters for DValidator. */
final class CoreAdapterBuilder {
  public static final ValidationAdapter NOOP = (type, req, propertyName) -> true;
  private final DValidator context;
  private final List<ValidationContext.AdapterFactory> factories = new ArrayList<>();
  private final List<ValidationContext.AnnotationFactory> annotationFactories = new ArrayList<>();
  private final Map<Object, ValidationAdapter<?>> adapterCache = new ConcurrentHashMap<>();

  CoreAdapterBuilder(
      DValidator context,
      List<ValidationContext.AdapterFactory> userFactories,
      List<ValidationContext.AnnotationFactory> userAnnotationFactories,
      Supplier<Clock> clockSupplier,
      Duration temporalTolerance) {
    this.context = context;
    this.factories.addAll(userFactories);
    this.annotationFactories.addAll(userAnnotationFactories);
    this.annotationFactories.add(BasicAdapters.FACTORY);
    this.annotationFactories.add(NumberAdapters.FACTORY);
    this.annotationFactories.add(new FuturePastAdapterFactory(clockSupplier, temporalTolerance));
  }

  /** Return the adapter from cache if exists else return null. */
  @SuppressWarnings("unchecked")
  <T> ValidationAdapter<T> get(Object cacheKey) {
    return (ValidationAdapter<T>) adapterCache.get(cacheKey);
  }

  /** Build for the simple non-annotated type case. */
  <T> ValidationAdapter<T> build(Type type) {
    return build(type, type);
  }

  <T> ValidationAdapter<T> annotationAdapter(
      Class<? extends Annotation> cls, Map<String, Object> attributes, Set<Class<?>> groups) {
    return buildAnnotation(cls, attributes,groups);
  }

  /** Build given type and annotations. */
  // TODO understand that lookup chain stuff
  @SuppressWarnings("unchecked")
  <T> ValidationAdapter<T> build(Type type, Object cacheKey) {
    // Ask each factory to create the validation adapter.
    for (final ValidationContext.AdapterFactory factory : factories) {
      final var result = (ValidationAdapter<T>) factory.create(type, context);
      if (result != null) {
        return result;
      }
    }
    throw new IllegalArgumentException("No ValidationAdapter for " + type + ". Perhaps needs @ValidPojo or @ValidPojo.Import?");
  }

  /**
   * Build given type and annotations.
   *
   * @param groups
   */
  // TODO understand that lookup chain stuff
  @SuppressWarnings("unchecked")
  <T> ValidationAdapter<T> buildAnnotation(
      Class<? extends Annotation> cls,
      Map<String, Object> attributes,
      @Nullable Set<Class<?>> groups) {

    final var paramGroups =
        groups != null ? groups : (Set<Class<?>>) attributes.getOrDefault("groups", Set.of());

    // Ask each factory to create the validation adapter.
    for (final ValidationContext.AnnotationFactory factory : annotationFactories) {
      final var result =
          (ValidationAdapter<T>) factory.create(cls, context, paramGroups, attributes);
      if (result != null) {
        return result;
      }
    }
    // unknown annotations have noop
    return NOOP;
  }
}
