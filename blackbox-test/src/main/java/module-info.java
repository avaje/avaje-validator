module blackbox.test {

  requires io.avaje.validation.http;
  requires io.avaje.validation.contraints;
  requires jakarta.validation;
  requires jakarta.inject;
  provides io.avaje.validation.Validator.GeneratedComponent with example.avaje.valid.GeneratedValidatorComponent;
  provides io.avaje.inject.spi.InjectExtension with example.avaje.AvajeModule;
}
