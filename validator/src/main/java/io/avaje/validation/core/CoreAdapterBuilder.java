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

import io.avaje.validation.adapter.ConstraintAdapter;
import io.avaje.validation.groups.Default;
import org.jspecify.annotations.Nullable;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.adapters.BasicAdapters;
import io.avaje.validation.core.adapters.FuturePastAdapterFactory;
import io.avaje.validation.core.adapters.NumberAdapters;
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
    // bootstrap the builtin factories potentially with default adapters
    // that use the default group and default message
    var requestBuilder = new RequestBuilder(context);
    this.annotationFactories.add(BasicAdapters.factory(requestBuilder));
    this.annotationFactories.add(NumberAdapters.FACTORY);
    this.annotationFactories.add(new FuturePastAdapterFactory(clockSupplier, temporalTolerance));
  }

  /** Return the adapter from cache if exists creating the adapter if required. */
  @SuppressWarnings("unchecked")
  <T> ValidationAdapter<T> build(Type type) {
    var adapter = adapterCache.get(type);
    if (adapter != null) {
      return (ValidationAdapter<T>)adapter;
    }
    ValidationAdapter<T> newValidator = buildForType(type);
    adapterCache.put(type, newValidator);
    return newValidator;
  }

  /** Build for the simple non-annotated type case. */
  @SuppressWarnings("unchecked")
  private <T> ValidationAdapter<T> buildForType(Type type) {
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
   */
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

  private static final class RequestBuilder implements ValidationContext.RequestBuilder {

    private final DValidator context;

    private RequestBuilder(DValidator context) {
      this.context = context;
    }

    @Override
    public ValidationContext.AdapterCreateRequest defaultRequest(String defaultMessage) {
      // ConstraintAdapter.class is just a placeholder and not meaningful
      return new Request(context, ConstraintAdapter.class, DEFAULT_GROUP, Map.of("message", defaultMessage));
    }
  }

  private record Request(

    ValidationContext ctx,
    Class<? extends Annotation> annotationType,
    Set<Class<?>> groups,
    Map<String, Object> attributes

  ) implements ValidationContext.AdapterCreateRequest {

    @Override
    public boolean isDefaultGroupOnly() {
      return DEFAULT_GROUP.equals(groups);
    }

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
