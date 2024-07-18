/** Avaje Validation Library */
module io.avaje.validation {
  exports io.avaje.validation;
  exports io.avaje.validation.adapter;
  exports io.avaje.validation.groups;
  exports io.avaje.validation.spi;

  requires io.avaje.applog;
  requires static io.avaje.inject;
  requires static io.avaje.inject.aop;
  requires static io.avaje.spi;
  requires static transitive org.jspecify;

  uses io.avaje.validation.spi.ValidationExtension;
}
