package io.avaje.validation.core;

final class NoopMessageInterpolator implements MessageInterpolator {

  @Override
  public String interpolate(String string) {
    return string;
  }
}
