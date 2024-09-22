package io.avaje.validation.core;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationRequest;

public class NoOpValidator implements ValidationAdapter, ValidationAdapter.Primitive {

  public static final NoOpValidator INSTANCE = new NoOpValidator();

  @Override
  public boolean validate(Object value, ValidationRequest req, String propertyName) {

    return true;
  }

  @Override
  public Primitive primitive() {
    return this;
  }

  @Override
  public boolean validate(boolean value, ValidationRequest req, String propertyName) {

    return true;
  }

  @Override
  public boolean validate(byte value, ValidationRequest req, String propertyName) {

    return true;
  }

  @Override
  public boolean validate(char value, ValidationRequest req, String propertyName) {

    return true;
  }

  @Override
  public boolean validate(double value, ValidationRequest req, String propertyName) {

    return true;
  }

  @Override
  public boolean validate(float value, ValidationRequest req, String propertyName) {

    return true;
  }

  @Override
  public boolean validate(int value, ValidationRequest req, String propertyName) {

    return true;
  }

  @Override
  public boolean validate(long value, ValidationRequest req, String propertyName) {

    return true;
  }

  @Override
  public boolean validate(short value, ValidationRequest req, String propertyName) {

    return true;
  }
}
