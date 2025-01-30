package io.avaje.validation.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.adapters.BasicAdapters;
import io.avaje.validation.core.adapters.FuturePastAdapterFactory;
import io.avaje.validation.core.adapters.NumberAdapters;
import io.avaje.validation.groups.Default;
import io.avaje.validation.spi.AdapterFactory;
import io.avaje.validation.spi.AnnotationFactory;

/** Builds and caches the ValidationAdapter adapters for DValidator. */
final class CoreAdapterBuilder {

  private static final Set<Class<?>> DEFAULT_GROUP = Set.of(Default.class);
  private final DValidator context;
  private final List<AdapterFactory> factories = new ArrayList<>();
  private final List<AnnotationFactory> annotationFactories = new ArrayList<>();
  private final Map<Type, ValidationAdapter<?>> adapterCache = new ConcurrentHashMap<>();

  CoreAdapterBuilder(
      DValidator context,
      List<AdapterFactory> userFactories,
      List<AnnotationFactory> userAnnotationFactories,
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
  <T> ValidationAdapter<T> get(Type cacheKey) {
    return (ValidationAdapter<T>) adapterCache.get(cacheKey);
  }

  /** Build for the simple non-annotated type case. */
  @SuppressWarnings("unchecked")
  <T> ValidationAdapter<T> build(Type type) {
    // Ask each factory to create the validation adapter.
    for (final AdapterFactory factory : factories) {
      final var result = (ValidationAdapter<T>) factory.create(type, context);
      if (result != null) {
        return result;
      }
    }
    throw new IllegalArgumentException("No ValidationAdapter for " + type + ". Perhaps needs @Valid or @Valid.Import?");
  }

  <T> ValidationAdapter<T> annotationAdapter(
      Class<? extends Annotation> cls, Map<String, Object> attributes, Set<Class<?>> groups) {
    return buildAnnotation(cls, attributes, groups);
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

    var paramGroups =
        groups != null ? groups : (Set<Class<?>>) attributes.getOrDefault("groups", DEFAULT_GROUP);

    if (paramGroups.isEmpty()) {
      paramGroups = DEFAULT_GROUP;
    }

    var request = new Request(context, cls, paramGroups, attributes);
    // Ask each factory to create the validation adapter.
    for (final var factory : annotationFactories) {
      final var result = (ValidationAdapter<T>) factory.create(request);
      if (result != null) {
        return result;
      }
    }
    // unknown annotations have noop
    return NoOpValidator.INSTANCE;
  }

  record Request(

    ValidationContext ctx,
    Class<? extends Annotation> annotationType,
    Set<Class<?>> groups,
    Map<String, Object> attributes

  ) implements ValidationContext.AdapterCreateRequest {

    @Override
    public String targetType() {
      return attribute("_type");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T attribute(String key) {
      return (T) attributes.get(key);
    }

    @Override
    public Request withValue(long value) {
      Map<String, Object> newAttributes = new HashMap<>(attributes);
      newAttributes.put("value", value);
      //newAttributes.put("_type", "Long");
      return new Request(ctx, annotationType, groups, newAttributes);
    }

    @Override
    public ValidationContext.Message message() {
      return ctx.message(attributes);
    }

    @Override
    public ValidationContext.Message message(String messageKey, Object... extraKeyValues) {
      Map<String, Object> newAttributes = new HashMap<>(attributes);
      newAttributes.put("message", messageKey);
      if (extraKeyValues != null) {
        for (int i = 0; i < extraKeyValues.length; i += 2) {
          newAttributes.put(String.valueOf(extraKeyValues[i]), extraKeyValues[i + 1]);
        }
      }
      return ctx.message(newAttributes);
    }
  }
}
