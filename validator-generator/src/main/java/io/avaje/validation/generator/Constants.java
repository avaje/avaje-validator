package io.avaje.validation.generator;

import java.util.Set;

final class Constants {

  static final String META_INF_COMPONENT =
      "META-INF/services/io.avaje.validation.Validator$GeneratedComponent";
  public static final String VALID_SPI = "io.avaje.validation.spi.*";
  public static final String VALIDATOR = "io.avaje.validation.Validator";
  public static final Set<String> VALID_ANNOTATIONS =
      Set.of(
          ValidPojoPrism.PRISM_TYPE,
          ValidPrism.PRISM_TYPE,
          JavaxValidPrism.PRISM_TYPE,
          JakartaValidPrism.PRISM_TYPE);
}
