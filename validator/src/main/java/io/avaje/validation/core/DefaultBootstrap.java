package io.avaje.validation.core;

import io.avaje.validation.Validator;

/** Default bootstrap of Validator. */
public final class DefaultBootstrap {
  private DefaultBootstrap() {}

  /** Create the Validator.Builder. */
  public static Validator.Builder builder() {
    return new DValidator.DBuilder();
  }

  public static Validator instance() {
    return DValidator.DBuilder.DEFAULT;
  }
}
