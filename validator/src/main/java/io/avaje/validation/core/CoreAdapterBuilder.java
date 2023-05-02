package io.avaje.validation.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.avaje.validation.adapter.AnnotationValidationAdapter;
import io.avaje.validation.adapter.AnnotationValidatorFactory;
import io.avaje.validation.adapter.ValidationAdapter;

/** Builds and caches the ValidationAdapter adapters for DValidator. */
final class CoreAdapterBuilder {

  private final DValidator context;
  private final List<ValidationAdapter.Factory> factories = new ArrayList<>();
  private final List<AnnotationValidatorFactory> annotationFactories = new ArrayList<>();
  private final Map<Object, ValidationAdapter<?>> adapterCache = new ConcurrentHashMap<>();
  private final MessageInterpolator interpolator;

  CoreAdapterBuilder(
      DValidator context,
      List<ValidationAdapter.Factory> userFactories,
      List<AnnotationValidatorFactory> userAnnotationFactories) {
    this.context = context;
    this.factories.addAll(userFactories);
    this.annotationFactories.addAll(userAnnotationFactories);
    this.annotationFactories.add(JakartaTypeAdapters.FACTORY);
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

  public <T> AnnotationValidationAdapter<T> annotationAdapter(Class<? extends Annotation> cls) {
    return (AnnotationValidationAdapter<T>) buildAnnotation(cls);
  }

  /** Build given type and annotations. */
  // TODO understand that lookup chain stuff
  @SuppressWarnings("unchecked")
  <T> ValidationAdapter<T> build(Type type, Object cacheKey) {

    // Ask each factory to create the JSON adapter.
    for (final ValidationAdapter.Factory factory : factories) {
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
  <T> AnnotationValidationAdapter<T> buildAnnotation(Class<? extends Annotation> cls) {

    // Ask each factory to create the JSON adapter.
    for (final AnnotationValidatorFactory factory : annotationFactories) {
      final var result =
          (AnnotationValidationAdapter<T>) factory.create(cls, context, interpolator);
      if (result != null) {

        return result;
      }
    }
    // unknown annotation have noop
    return new NoopAnnotationValidator<>();
  }
}
