module io.avaje.validation.generator {

  requires java.compiler;
  requires io.avaje.validation;
  requires static io.avaje.prism;
  requires static java.validation;
  requires static jakarta.validation;

  provides javax.annotation.processing.Processor with io.avaje.validation.generator.Processor;
}
