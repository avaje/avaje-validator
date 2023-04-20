/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.avaje.validation.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.avaje.validation.ValidationAdapter;

/** Builds and caches the ValidationAdapter adapters for DValidator. */
final class CoreAdapterBuilder {

  private final DValidator context;
  private final List<ValidationAdapter.Factory> factories = new ArrayList<>();
  private final List<AnnotationValidationAdapter.Factory> annotationFactories = new ArrayList<>();
  private final Map<Object, ValidationAdapter<?>> adapterCache = new ConcurrentHashMap<>();
  private final MessageInterpolator interpolator;

  CoreAdapterBuilder(
      DValidator context,
      List<ValidationAdapter.Factory> userFactories,
      List<AnnotationValidationAdapter.Factory> userAnnotationFactories) {
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
    for (final AnnotationValidationAdapter.Factory factory : annotationFactories) {
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
