package io.avaje.validation.adapter;

/** Validation adapter combining two primitive adapters. */
final class CombinedPrimitiveAdapter<T>
    implements ValidationAdapter<T>, ValidationAdapter.Primitive {

  private final ValidationAdapter<T> first;
  private final ValidationAdapter<? super T> second;
  private final ValidationAdapter.Primitive firstPrimitive;
  private final ValidationAdapter.Primitive secondPrimitive;

  CombinedPrimitiveAdapter(ValidationAdapter<T> first, ValidationAdapter<? super T> second) {
    this.first = first;
    this.second = second;
    this.firstPrimitive = (ValidationAdapter.Primitive) first;
    this.secondPrimitive = (ValidationAdapter.Primitive) second;
  }

  @Override
  public boolean validate(T value, ValidationRequest req, String propertyName) {
    return first.validate(value, req, propertyName) && second.validate(value, req, propertyName);
  }

  @Override
  public Primitive primitive() {
    return this;
  }

  @Override
  public boolean validate(boolean value, ValidationRequest req, String propertyName) {
    return firstPrimitive.validate(value, req, propertyName)
        && secondPrimitive.validate(value, req, propertyName);
  }

  @Override
  public boolean validate(byte value, ValidationRequest req, String propertyName) {
    return firstPrimitive.validate(value, req, propertyName)
        && secondPrimitive.validate(value, req, propertyName);
  }

  @Override
  public boolean validate(char value, ValidationRequest req, String propertyName) {
    return firstPrimitive.validate(value, req, propertyName)
        && secondPrimitive.validate(value, req, propertyName);
  }

  @Override
  public boolean validate(double value, ValidationRequest req, String propertyName) {
    return firstPrimitive.validate(value, req, propertyName)
        && secondPrimitive.validate(value, req, propertyName);
  }

  @Override
  public boolean validate(float value, ValidationRequest req, String propertyName) {
    return firstPrimitive.validate(value, req, propertyName)
        && secondPrimitive.validate(value, req, propertyName);
  }

  @Override
  public boolean validate(int value, ValidationRequest req, String propertyName) {
    return firstPrimitive.validate(value, req, propertyName)
        && secondPrimitive.validate(value, req, propertyName);
  }

  @Override
  public boolean validate(long value, ValidationRequest req, String propertyName) {
    return firstPrimitive.validate(value, req, propertyName)
        && secondPrimitive.validate(value, req, propertyName);
  }

  @Override
  public boolean validate(short value, ValidationRequest req, String propertyName) {
    return firstPrimitive.validate(value, req, propertyName)
        && secondPrimitive.validate(value, req, propertyName);
  }
}
