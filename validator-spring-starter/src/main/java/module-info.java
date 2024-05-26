module io.avaje.validation.spring {

  exports io.avaje.validation.spring.aspect;
  exports io.avaje.validation.spring.validator;

  requires transitive io.avaje.validation;
  requires transitive jakarta.inject;
  requires transitive org.aspectj.weaver;
  requires transitive spring.beans;
  requires transitive spring.context;
  requires transitive spring.boot.autoconfigure;

}
