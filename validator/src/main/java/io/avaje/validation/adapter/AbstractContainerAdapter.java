package io.avaje.validation.adapter;
/**
 * Adapter that validates container types.
 *
 * @param <T> the type we are validating
 */
public abstract class AbstractContainerAdapter<T> implements ValidationAdapter<T> {

	/**
	 * Adapter placed on the the container type
	 */
  protected final ValidationAdapter<T> initalAdapter;

  protected ValidationAdapter<Object> multiAdapter;

  /** @param initialAdapter initial adapter that can be used to validate the container itself */
  protected AbstractContainerAdapter(ValidationAdapter<T> initialAdapter) {
    this.initalAdapter = initialAdapter;
  }

  /**
   * Compose the given adapter with the multiAdapter of this AbstractContainerAdapter for validating
   * multiple items.
   */
  @SuppressWarnings("unchecked")
  public AbstractContainerAdapter<T> andThenMulti(ValidationAdapter<?> adapter) {
    this.multiAdapter =
        this.multiAdapter != null
            ? multiAdapter.andThen((ValidationAdapter<Object>) adapter)
            : (ValidationAdapter<Object>) adapter;
    return this;
  }

  /** Execute validations for all items in the given iterable */
  protected boolean validateAll(Iterable<Object> value, ValidationRequest req, String propertyName) {
    if (value == null) {
      return true;
    }
    if (propertyName != null) {
      req.pushPath(propertyName);
    }
    int index = 0;
    for (final var element : value) {
      multiAdapter.validate(element, req, "[" + index++);
    }
    if (propertyName != null) {
      req.popPath();
    }
    return true;
  }

  /** Execute validations for all items in the given array */
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
      multiAdapter.validate(element, req, String.valueOf(index));
    }
    if (propertyName != null) {
      req.popPath();
    }
    return true;
  }
}
