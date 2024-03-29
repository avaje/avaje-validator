package io.avaje.validation.adapter;

/**
 * Adapter that validates container types.
 *
 * @param <T> the type we are validating
 */
public abstract class ContainerAdapter<T> implements ValidationAdapter<T> {

  /** Adapter placed on the container type */
  protected final ValidationAdapter<T> initalAdapter;

  protected ValidationAdapter<Object> multiAdapter;

  /** @param initialAdapter initial adapter that can be used to validate the container itself */
  protected ContainerAdapter(ValidationAdapter<T> initialAdapter) {
    this.initalAdapter = initialAdapter;
  }

  /**
   * Compose the given adapter with the multiAdapter of this ContainerAdapter for validating
   * multiple items.
   */
  @SuppressWarnings("unchecked")
  public ContainerAdapter<T> andThenMulti(ValidationAdapter<?> adapter) {
    this.multiAdapter =
        this.multiAdapter != null
            ? multiAdapter.andThen((ValidationAdapter<Object>) adapter)
            : (ValidationAdapter<Object>) adapter;
    return this;
  }

  /** Execute validations for all items in the given iterable */
  protected void validateAll(Iterable<Object> value, ValidationRequest req, String propertyName) {
    if (value == null || multiAdapter == null) {
      return;
    }
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = 0;
    for (final var element : value) {
      multiAdapter.validate(element, req, "[" + index);
      index++;
    }
    if (propertyName != null) {
      req.popPath();
    }
  }

  /** Execute validations for all items in the given array */
  protected void validateArray(Object[] value, ValidationRequest req, String propertyName) {
    if (value == null || multiAdapter == null) {
      return;
    }
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = 0;
    for (final Object element : value) {
      multiAdapter.validate(element, req, "[" + index);
      index++;
    }
    if (propertyName != null) {
      req.popPath();
    }
  }
}
