package io.avaje.validation.core;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import io.avaje.validation.ValidationAdapter;
import io.avaje.validation.ValidationType;
import io.avaje.validation.stream.ConstraintViolation;

class DValidationType<T> implements ValidationType<T> {

  protected final DValidator jsonb;
  protected final Type type;
  protected final ValidationAdapter<T> adapter;

  DValidationType(DValidator jsonb, Type type, ValidationAdapter<T> adapter) {
    this.jsonb = jsonb;
    this.type = type;
    this.adapter = adapter;
  }

  @Override
  public Set<ConstraintViolation> validate(T object) {
    final Set<ConstraintViolation> violations = new HashSet<>();
    validate(object, violations);
    violations.remove(null);
    return violations;
  }

  @Override
  public void validate(T object, Set<ConstraintViolation> violations) {
    adapter.validate(object, violations);
  }
}
