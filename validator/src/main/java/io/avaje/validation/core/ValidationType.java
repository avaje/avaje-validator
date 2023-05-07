package io.avaje.validation.core;

import io.avaje.validation.ConstraintViolationException;
import io.avaje.lang.Nullable;
import java.util.Locale;

public interface ValidationType<T> {

  void validate(T object, @Nullable Locale locale) throws ConstraintViolationException;

}
