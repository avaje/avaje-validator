/** Avaje Validation Library */
module io.avaje.validation {
  exports io.avaje.validation;
  exports io.avaje.validation.adapter;
  exports io.avaje.validation.groups;
  exports io.avaje.validation.spi;

  requires io.avaje.lang;
  requires io.avaje.applog;
  requires static io.avaje.inject;

  uses io.avaje.validation.Validator.GeneratedComponent;
  uses io.avaje.validation.spi.MessageInterpolator;
  uses io.avaje.validation.spi.ValidatorCustomizer;
  uses io.avaje.validation.adapter.ValidationContext.AdapterFactory;
  uses io.avaje.validation.adapter.ValidationContext.AnnotationFactory;
}
