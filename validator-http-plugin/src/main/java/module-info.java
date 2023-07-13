module io.avaje.validation.http {

  requires transitive io.avaje.validation.plugin;
  requires transitive io.avaje.http.api;

  provides io.avaje.inject.spi.Plugin with io.avaje.validation.http.DefaultValidatorProvider;
}
