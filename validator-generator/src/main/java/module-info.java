import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheType;

@JStacheConfig(type = JStacheType.STACHE)
module io.avaje.validation.generator {

  requires java.compiler;
  requires static io.avaje.validation;
  requires static io.avaje.prism;
  requires static io.avaje.http.api;
  requires static io.avaje.validation.contraints;
  requires static io.jstach.jstache;
  requires static java.validation;
  requires static jakarta.validation;

  provides javax.annotation.processing.Processor with io.avaje.validation.generator.ValidationProcessor;
}
