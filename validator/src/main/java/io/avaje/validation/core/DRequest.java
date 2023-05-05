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
  @Nullable
  private final Locale locale;

  DRequest(DValidator validator, @Nullable Locale locale) {
    this.validator = validator;
    this.locale = locale;
  }

  private String currentPath() {
    StringJoiner joiner = new StringJoiner(".");
    final var descendingIterator = pathStack.descendingIterator();
    while (descendingIterator.hasNext()) {
      joiner.add(descendingIterator.next());
    }
    return joiner.toString();
  }


  @Override
  public void addViolation(String msg, String propertyName) {
    violations.add(new ConstraintViolation(currentPath(), propertyName, msg));
  }

  @Override
  public void addViolation(ValidationContext.Message msg, String propertyName) {
    String message = validator.interpolate(msg, locale);
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
}
