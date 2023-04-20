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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

import io.avaje.validation.adapter.AnnotationValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

final class JakartaTypeAdapters {

  @SuppressWarnings({"unchecked", "rawtypes"})
  static final AnnotationValidationAdapter.Factory FACTORY =
      (annotationType, validator, interpolator) -> {
        if (annotationType == AssertTrue.class) return new AssertTrueAdapter(interpolator);
        if (annotationType == NotBlank.class) return new NotBlankAdapter(interpolator);
        if (annotationType == Past.class) return new PastAdapter(interpolator);
        return null;
      };

  private static final class PastAdapter implements AnnotationValidationAdapter<TemporalAccessor> {

    private String message;
    private final MessageInterpolator interpolator;

    public PastAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<TemporalAccessor> init(
        Map<String, String> annotationValueMap) {
      message = interpolator.interpolate(annotationValueMap.get("message"));
      return this;
    }

    @Override
    public boolean validate(TemporalAccessor temporalAccessor, ValidationRequest req, String propertyName) {
      if (temporalAccessor == null) {
        req.addViolation(message, propertyName);
        return false;
      }
      if (temporalAccessor instanceof LocalDate) {
        if (LocalDate.from(temporalAccessor).isAfter(LocalDate.now())) {
          req.addViolation(message, propertyName);
          return false;
        }
      } else if (temporalAccessor instanceof LocalTime) {
        final LocalTime localTime = (LocalTime) temporalAccessor;
        // handle LocalTime

        // TODO do the rest of them
      }
      return true;
    }
  }

  private static final class NotBlankAdapter implements AnnotationValidationAdapter<String> {

    private String message;
    private final MessageInterpolator interpolator;

    public NotBlankAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<String> init(Map<String, String> annotationValueMap) {
      message = interpolator.interpolate(annotationValueMap.get("message"));
      return this;
    }

    @Override
    public boolean validate(String str, ValidationRequest req, String propertyName) {
      if (str == null || str.isBlank()) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }

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
    public boolean validate(Boolean type, ValidationRequest req, String propertyName) {
      if (Boolean.FALSE.equals(type)) {
        req.addViolation(message, propertyName);
        return false;
      }
      return true;
    }
  }
}
