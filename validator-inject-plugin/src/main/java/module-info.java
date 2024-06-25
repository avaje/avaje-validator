module io.avaje.validation.plugin {

  requires transitive io.avaje.validation;
  requires transitive io.avaje.inject;
  requires transitive io.avaje.inject.aop;

  provides io.avaje.inject.spi.InjectExtension with io.avaje.validation.inject.spi.DefaultValidatorProvider;
}
