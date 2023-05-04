module io.avaje.validation {

  exports io.avaje.validation;
  exports io.avaje.validation.adapter;
  exports io.avaje.validation.spi;

   uses io.avaje.validation.spi.Bootstrap;
   uses io.avaje.validation.Validator.GeneratedComponent;
   uses io.avaje.validation.core.MessageInterpolator;
   uses io.avaje.validation.adapter.ValidatorComponent;
  uses io.avaje.validation.adapter.ValidationContext.AdapterFactory;
  uses io.avaje.validation.adapter.ValidationContext.AnnotationFactory;



}
