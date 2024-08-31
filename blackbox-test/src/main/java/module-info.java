@io.avaje.inject.InjectModule(name="GeneratedModule")
module blackbox.test {

  requires io.avaje.inject.aop;
  requires io.avaje.validation.http;
  requires io.avaje.validation.contraints;
  requires jakarta.validation;
  requires jakarta.inject;
  requires org.jspecify;

  provides io.avaje.validation.spi.ValidationExtension with example.avaje.valid.GeneratedValidatorComponent;
  provides io.avaje.inject.spi.InjectExtension with example.avaje.GeneratedModule;
}
