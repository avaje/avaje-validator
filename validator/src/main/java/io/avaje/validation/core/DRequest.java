package io.avaje.validation.core;

import io.avaje.lang.Nullable;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;
import io.avaje.validation.groups.Default;

import java.util.*;

final class DRequest implements ValidationRequest {

  private static final List<Class<?>> DEFAULT_GROUP = List.of(Default.class);

  private final ArrayDeque<String> pathStack = new ArrayDeque<>();

  private final Set<ConstraintViolation> violations = new LinkedHashSet<>();

  private final DValidator validator;
  private final boolean failfast;
  private final List<Class<?>> groups;
  @Nullable private final Locale locale;

  DRequest(DValidator validator, boolean failfast, @Nullable Locale locale, List<Class<?>> groups) {
    this.validator = validator;
    this.failfast = failfast;
    this.locale = locale;
    this.groups = !groups.isEmpty() ? groups : DEFAULT_GROUP;
  }

  private String currentPath() {
    if (pathStack.isEmpty()) {
      return "";
    }
    final StringBuilder sb = new StringBuilder(70);
    final var descendingIterator = pathStack.descendingIterator();
    while (descendingIterator.hasNext()) {
      final String next = descendingIterator.next();
      if (next.charAt(0) == '[') {
        sb.append(next).append(']');
      } else {
        if (!sb.isEmpty()) {
          sb.append('.');
        }
        sb.append(next);
      }
    }
    return sb.append('.').toString();
  }

  @Override
  public void addViolation(ValidationContext.Message msg, String propertyName) {
    final String message = validator.interpolate(msg, locale);
    violations.add(new ConstraintViolation(currentPath() + propertyName, propertyName, message));
    if (failfast) {
      throwWithViolations();
    }
  }

  @Override
  public void pushPath(String path) {
    pathStack.push(path);
  }

  @Override
  public void popPath() {
    pathStack.pop();
  }

  @Override
  public void throwWithViolations() {
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations, groups);
    }
  }

  @Override
  public List<Class<?>> groups() {
    return groups;
  }

  @Override
  public String toString() {
    return violations.toString();
  }

  @Override
  public boolean hasViolations() {
    return !violations.isEmpty();
  }
}
