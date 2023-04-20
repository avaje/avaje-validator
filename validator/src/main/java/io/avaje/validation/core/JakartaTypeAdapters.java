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

import java.util.Map;

import io.avaje.validation.ConstraintViolation;
import jakarta.validation.constraints.AssertTrue;
final class JakartaTypeAdapters {

  @SuppressWarnings({"unchecked", "rawtypes"})
  static final AnnotationValidationAdapter.Factory FACTORY =
      (type, jsonb, interpolator) -> {
        if (type == AssertTrue.class) return new AssertTrueAdapter(interpolator);
        return null;
      };

  private static final class AssertTrueAdapter implements AnnotationValidationAdapter<Boolean> {

    private String message;
    private final MessageInterpolator interpolator;

    public AssertTrueAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

  @Override
    public AnnotationValidationAdapter<Boolean> init(Map<String, String> annotationValueMap) {
      message = interpolator.interpolate(annotationValueMap.get("message"));
      return this;
    }

    @Override
    public ConstraintViolation validate(Boolean type) {
      if (type) return null;

      return new ConstraintViolation(message);
    }
  }
}
