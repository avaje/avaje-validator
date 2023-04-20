package io.avaje.validation.spi;

import io.avaje.validation.Validator;

/**
 * Bootstrap Validator.
 */
public interface Bootstrap {

  /**
   * Create and return a Builder (with an underling SPI implementation).
   * <p>
   * The default implementation uses Jackson Core.
   */
  Validator.Builder builder();
}
