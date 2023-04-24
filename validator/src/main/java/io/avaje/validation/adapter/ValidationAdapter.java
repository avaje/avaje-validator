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
package io.avaje.validation.adapter;

import io.avaje.validation.AnnotationValidationAdapter;
import io.avaje.validation.Validator;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

public interface ValidationAdapter<T> {

  /** Return true if validation should recurse */
  boolean validate(T value, ValidationRequest req, String propertyName);

  default boolean validate(T value, ValidationRequest req) {
    return validate(value, req, null);
  }

  default boolean validateAll(Collection<T> value, ValidationRequest req, String propertName) {
    if (propertName != null) {
      req.pushPath(propertName);
    }
    int index = -1;
    for (T element : value) {
      validate(element, req, String.valueOf(++index));
    }
    if (propertName != null) {
      req.popPath();
    }
    return true;
  }

  default boolean validateAll(T[] value, ValidationRequest req, String propertName) {
    if (propertName != null) {
      req.pushPath(propertName);
    }
    int index = -1;
    for (T element : value) {
      validate(element, req, String.valueOf(++index));
    }
    if (propertName != null) {
      req.popPath();
    }
    return true;
  }

  default AnnotationValidationAdapter<T> andThen(ValidationAdapter<? super T> after) {
    Objects.requireNonNull(after);
    return (value, req, propertyName) -> {
      if (validate(value, req, propertyName)) {
        return after.validate(value, req, propertyName);
      }
      return true;
    };
  }

  /** Factory for creating a ValidationAdapter. */
  public interface Factory {

    /**
     * Create and return a ValidationAdapter given the type and annotations or return null.
     *
     * <p>Returning null means that the adapter could be created by another factory.
     */
    ValidationAdapter<?> create(Type type, Validator jsonb);
  }
}
