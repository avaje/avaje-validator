package io.avaje.validation.generator;

import java.util.Set;

final class Constants {

  static final String META_INF_COMPONENT =
      "META-INF/services/io.avaje.validation.spi.ValidationExtension";
  static final String META_INF_CUSTOMIZER =
      "META-INF/services/io.avaje.validation.spi.ValidatorCustomizer";
  public static final String VALID_SPI = "io.avaje.validation.spi.*";
  public static final String VALIDATOR = "io.avaje.validation.Validator";
  public static final String COMPONENT = "io.avaje.inject.Component";
  static final String SINGLETON_JAKARTA = "jakarta.inject.Singleton";
  static final String SINGLETON_JAVAX = "javax.inject.Singleton";

  public static final Set<String> VALID_ANNOTATIONS =
      Set.of(
          AvajeValidPrism.PRISM_TYPE,
          HttpValidPrism.PRISM_TYPE,
          JavaxValidPrism.PRISM_TYPE,
          JakartaValidPrism.PRISM_TYPE);
}
