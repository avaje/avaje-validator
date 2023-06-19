package io.avaje.validation.core;

import io.avaje.lang.Nullable;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;

import java.util.*;

final class DRequest implements ValidationRequest {

  private final ArrayDeque<String> pathStack = new ArrayDeque<>();

  private final Set<ConstraintViolation> violations = new LinkedHashSet<>();

  private final DValidator validator;
  @Nullable private final Locale locale;

  private final List<Class<?>> groups;

  DRequest(DValidator validator, @Nullable Locale locale, List<Class<?>> groups) {
    this.validator = validator;
    this.locale = locale;
    this.groups = groups;
  }

  private String currentPath() {
    final StringJoiner joiner = new StringJoiner(".");
    final var descendingIterator = pathStack.descendingIterator();
    while (descendingIterator.hasNext()) {
      joiner.add(descendingIterator.next());
    }
    return joiner.toString();
  }

  @Override
  public void addViolation(ValidationContext.Message msg, String propertyName) {
    final String message = validator.interpolate(msg, locale);
    violations.add(new ConstraintViolation(currentPath(), propertyName, message));
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
      throw new ConstraintViolationException(violations);
    }
  }

  @Override
  public List<Class<?>> groups() {
    return groups;
  }
}
