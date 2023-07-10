package io.avaje.validation.adapter;

public abstract class AbstractContainerAdapter<T> implements ValidationAdapter<T> {

  protected final ValidationAdapter<T> starterAdapter;
  private ValidationAdapter<Object> adapters;

  protected AbstractContainerAdapter(ValidationAdapter<T> starterAdapter) {
    this.starterAdapter = starterAdapter;
  }

  public AbstractContainerAdapter<T> andThenMulti(ValidationAdapter<?> adapter) {
    this.adapters =
        this.adapters != null
            ? adapters.andThen((ValidationAdapter<Object>) adapter)
            : (ValidationAdapter<Object>) adapter;
    return this;
  }

  protected boolean validateAll(
      Iterable<Object> value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = -1;
    for (final var element : value) {
      index++;
      adapters.validate(element, req, String.valueOf(index));
    }
    if (propertyName != null) {
      req.popPath();
    }
    return true;
  }

  protected boolean validateArray(Object[] value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = -1;
    for (final Object element : value) {
      index++;
      adapters.validate(element, req, String.valueOf(index));
    }
    if (propertyName != null) {
      req.popPath();
    }
    return true;
  }
}
