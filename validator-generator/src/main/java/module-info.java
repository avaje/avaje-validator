module io.avaje.validation.generator {

  requires java.compiler;
  requires io.avaje.validation.plugin;
  requires static io.avaje.prism;
  requires static io.avaje.http.api;
  requires static io.avaje.validation.contraints;
  requires static java.validation;
  requires static jakarta.validation;

  provides javax.annotation.processing.Processor with io.avaje.validation.generator.ValidationProcessor;
}
