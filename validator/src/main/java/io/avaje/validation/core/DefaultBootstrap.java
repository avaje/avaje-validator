package io.avaje.validation.core;

import io.avaje.validation.Validator;

/** Default bootstrap of Validator. */
public final class DefaultBootstrap {

  /** Create the Validator.Builder. */
  public static Validator.Builder builder() {
    return new DValidator.DBuilder();
  }
}
