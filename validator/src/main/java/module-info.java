/** Avaje Validation Library */
module io.avaje.validation {
  exports io.avaje.validation;
  exports io.avaje.validation.adapter;
  exports io.avaje.validation.groups;
  exports io.avaje.validation.spi;

  requires io.avaje.lang;
  requires io.avaje.applog;
  requires static io.avaje.inject;
  requires static io.avaje.inject.aop;
  requires static io.avaje.spi;

  uses io.avaje.validation.spi.ValidatorExtension;
}
