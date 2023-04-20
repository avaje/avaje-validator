package io.avaje.validation.core;

public class NooPMessageInterpolator implements MessageInterpolator {

  @Override
  public String interpolate(String string) {
    return string;
  }
}
