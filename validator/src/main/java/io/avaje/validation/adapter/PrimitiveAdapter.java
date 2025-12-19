package io.avaje.validation.adapter;

import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

/** Base class for primitive type adapters. */
public abstract class PrimitiveAdapter<T> extends AbstractConstraintAdapter<T>
    implements ValidationAdapter.Primitive {

  protected PrimitiveAdapter(AdapterCreateRequest request) {
    super(request);
  }

  @Override
  public final Primitive primitive() {
    return this;
  }

  /** Validate using primitive boolean. */
  public boolean isValid(boolean value) {
    throw unsupported("boolean");
  }

  /** Validate using primitive byte. */
  public boolean isValid(byte value) {
    throw unsupported("byte");
  }

  /** Validate using primitive char. */
  public boolean isValid(char value) {
    throw unsupported("char");
  }

  /** Validate using primitive double. */
  public boolean isValid(double value) {
    throw unsupported("double");
  }

  /** Validate using primitive float. */
  public boolean isValid(float value) {
    throw unsupported("float");
  }

  /** Validate using primitive int. */
  public boolean isValid(int value) {
    throw unsupported("int");
  }

  /** Validate using primitive long. */
  public boolean isValid(long value) {
    throw unsupported("long");
  }

  /** Validate using primitive short. */
  public boolean isValid(short value) {
    throw unsupported("short");
  }

  private UnsupportedOperationException unsupported(String type) {
    return new UnsupportedOperationException(
        "Validator " + toString() + " does not support primitive " + type);
  }

  @Override
  public final boolean validate(boolean value, ValidationRequest req, String propertyName) {
    if (!checkGroups(groups, req)) {
      return true;
    }
    if (!isValid(value)) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }

  @Override
  public final boolean validate(byte value, ValidationRequest req, String propertyName) {
    if (!checkGroups(groups, req)) {
      return true;
    }
    if (!isValid(value)) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }

  @Override
  public final boolean validate(char value, ValidationRequest req, String propertyName) {
    if (!checkGroups(groups, req)) {
      return true;
    }
    if (!isValid(value)) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }

  @Override
  public final boolean validate(double value, ValidationRequest req, String propertyName) {
    if (!checkGroups(groups, req)) {
      return true;
    }
    if (!isValid(value)) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }

  @Override
  public final boolean validate(float value, ValidationRequest req, String propertyName) {
    if (!checkGroups(groups, req)) {
      return true;
    }
    if (!isValid(value)) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }

  @Override
  public final boolean validate(int value, ValidationRequest req, String propertyName) {
    if (!checkGroups(groups, req)) {
      return true;
    }
    if (!isValid(value)) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }

  @Override
  public final boolean validate(long value, ValidationRequest req, String propertyName) {
    if (!checkGroups(groups, req)) {
      return true;
    }
    if (!isValid(value)) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }

  @Override
  public final boolean validate(short value, ValidationRequest req, String propertyName) {
    if (!checkGroups(groups, req)) {
      return true;
    }
    if (!isValid(value)) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }
}
