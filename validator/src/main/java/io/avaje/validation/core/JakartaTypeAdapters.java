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

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.avaje.validation.adapter.AnnotationValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;
import jakarta.validation.constraints.Size;

final class JakartaTypeAdapters {
  private JakartaTypeAdapters() {}

  @SuppressWarnings({"unchecked", "rawtypes"})
  static final AnnotationValidationAdapter.Factory FACTORY =
      (annotationType, validator, interpolator) -> {
        if (annotationType == AssertTrue.class) return new AssertTrueAdapter(interpolator);
        if (annotationType == NotBlank.class) return new NotBlankAdapter(interpolator);
        if (annotationType == Past.class) return new PastAdapter(interpolator);
        if (annotationType == Pattern.class) return new PatternAdapter(interpolator);
        if (annotationType == Size.class) return new SizeAdapter(interpolator);
        return null;
      };

  private static final class PatternAdapter implements AnnotationValidationAdapter<CharSequence> {

    private String message;
    private final MessageInterpolator interpolator;
    private Predicate<String> pattern;

    public PatternAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<CharSequence> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));

      int flags = 0;

      for (final var flag : (List<Flag>) annotationValueMap.get("flags")) {
        flags |= flag.getValue();
      }

      pattern =
          java.util.regex.Pattern.compile((String) annotationValueMap.get("regexp"), flags)
              .asMatchPredicate()
              .negate();

      return this;
    }

    @Override
    public boolean validate(CharSequence value, ValidationRequest req, String propertyName) {

      if (value == null || pattern.test(propertyName)) {

        req.addViolation(message, propertyName);
        return false;
      }

      return true;
    }
  }

  private static final class SizeAdapter implements AnnotationValidationAdapter<Object> {

    private String message;
    private final MessageInterpolator interpolator;
    private int min;
    private int max;

    public SizeAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<Object> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));
      min = (int) annotationValueMap.get("min");
      max = (int) annotationValueMap.get("max");
      return this;
    }

    @Override
    public boolean validate(Object value, ValidationRequest req, String propertyName) {

      if (value == null) {
        if (min != -1) {
          req.addViolation("CollectionNull", propertyName);
        }
        return false;
      }

      if (value instanceof CharSequence) {
        final var sequence = (CharSequence) value;
        final var len = sequence.length();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return false;
        }
      }

      if (value instanceof Collection<?>) {
        final var col = (Collection<?>) value;
        final var len = col.size();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      }

      if (value instanceof Map<?, ?>) {
        final var col = (Map<?, ?>) value;
        final var len = col.size();
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      }

      if (value.getClass().isArray()) {

        final var len = Array.getLength(value);
        if (len > max || len < min) {
          req.addViolation(message, propertyName);
          return len > 0;
        }
      }

      return true;
    }
  }

  private static final class PastAdapter implements AnnotationValidationAdapter<Object> {

    private String message;
    private final MessageInterpolator interpolator;

    public PastAdapter(MessageInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Override
    public AnnotationValidationAdapter<Object> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));
      return this;
    }

    @Override
    public boolean validate(Object obj, ValidationRequest req, String propertyName) {

      if (obj == null) {
        req.addViolation(message, propertyName);
        return false;
      } else if (obj instanceof Date) {
        final Date date = (Date) obj;
        if (date.after(Date.from(Instant.now()))) {
          req.addViolation(message, propertyName);
          return false;
        }
      } else if (obj instanceof TemporalAccessor) {

        final TemporalAccessor temporalAccessor = (TemporalAccessor) obj;
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
    public AnnotationValidationAdapter<String> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));
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
    public AnnotationValidationAdapter<Boolean> init(Map<String, Object> annotationValueMap) {
      message = interpolator.interpolate((String) annotationValueMap.get("message"));
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
