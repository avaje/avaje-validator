package io.avaje.validation.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.adapters.BasicAdapters;
import io.avaje.validation.core.adapters.NumberAdapters;

/** Builds and caches the ValidationAdapter adapters for DValidator. */
final class CoreAdapterBuilder {

  private final DValidator context;
  private final List<ValidationContext.AdapterFactory> factories = new ArrayList<>();
  private final List<ValidationContext.AnnotationFactory> annotationFactories = new ArrayList<>();
  private final Map<Object, ValidationAdapter<?>> adapterCache = new ConcurrentHashMap<>();
  private final MessageInterpolator interpolator;

  CoreAdapterBuilder(
      DValidator context,
      List<ValidationContext.AdapterFactory> userFactories,
      List<ValidationContext.AnnotationFactory> userAnnotationFactories) {
    this.context = context;
    this.factories.addAll(userFactories);
    this.annotationFactories.addAll(userAnnotationFactories);
    this.annotationFactories.add(BasicAdapters.FACTORY);
    this.annotationFactories.add(NumberAdapters.FACTORY);
    interpolator = context.interpolator();
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

  <T> ValidationAdapter<T> annotationAdapter(Class<? extends Annotation> cls, Map<String, Object> attributes) {
    return buildAnnotation(cls, attributes);
  }

  /** Build given type and annotations. */
  // TODO understand that lookup chain stuff
  @SuppressWarnings("unchecked")
  <T> ValidationAdapter<T> build(Type type, Object cacheKey) {
    // Ask each factory to create the validation adapter.
    for (final ValidationContext.AdapterFactory factory : factories) {
      final ValidationAdapter<T> result = (ValidationAdapter<T>) factory.create(type, context);
      if (result != null) {
        return result;
      }
    }
    throw new IllegalArgumentException(
        "No ValidationAdapter for " + type + ". Perhaps needs @ValidPojo or @ValidPojo.Import?");
  }

  /** Build given type and annotations. */
  // TODO understand that lookup chain stuff
  @SuppressWarnings("unchecked")
  <T> ValidationAdapter<T> buildAnnotation(Class<? extends Annotation> cls, Map<String, Object> attributes) {

    // Ask each factory to create the validation adapter.
    for (final ValidationContext.AnnotationFactory factory : annotationFactories) {
      final var result = (ValidationAdapter<T>) factory.create(cls, context, attributes);
      if (result != null) {
        return result;
      }
    }
    // unknown annotation have noop
    return NoopAnnotationValidator.INSTANCE;
  }
}
