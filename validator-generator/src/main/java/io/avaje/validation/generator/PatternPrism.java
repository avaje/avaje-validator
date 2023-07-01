package io.avaje.validation.generator;

import java.util.List;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import io.avaje.prism.GeneratePrism;

@GeneratePrism(
    value = io.avaje.validation.constraints.Pattern.class,
    name = "AvajePatternPrism",
    superInterfaces = PatternPrism.class)
@GeneratePrism(
    value = jakarta.validation.constraints.Pattern.class,
    name = "JakartaPatternPrism",
    superInterfaces = PatternPrism.class)
@GeneratePrism(
    value = javax.validation.constraints.Pattern.class,
    name = "JavaxPatternPrism",
    superInterfaces = PatternPrism.class)
@GeneratePrism(
    value = io.avaje.validation.constraints.Email.class,
    name = "AvajeEmailPrism",
    superInterfaces = PatternPrism.class)
@GeneratePrism(
    value = jakarta.validation.constraints.Email.class,
    name = "JakartaEmailPrism",
    superInterfaces = PatternPrism.class)
@GeneratePrism(
    value = javax.validation.constraints.Email.class,
    name = "JavaxEmailPrism",
    superInterfaces = PatternPrism.class)
public interface PatternPrism {

  /** @return the regular expression to match */
  String regexp();

  /** @return array of {@code RegexFlag}s considered when resolving the regular expression */
  List<String> flags();

  /** @return the error message template */
  String message();


  /** @return the error message template */
  List<TypeMirror> groups();

  static Optional<PatternPrism> isInstance(AnnotationMirror e) {

    return Optional.<PatternPrism>empty()
        .or(() -> AvajePatternPrism.getOptional(e))
        .or(() -> JakartaPatternPrism.getOptional(e))
        .or(() -> JavaxPatternPrism.getOptional(e))
        .or(() -> AvajeEmailPrism.getOptional(e))
        .or(() -> JakartaEmailPrism.getOptional(e))
        .or(() -> JavaxEmailPrism.getOptional(e));
  }

  static boolean isPresent(Element e) {
    return AvajePatternPrism.isPresent(e)
        || JakartaPatternPrism.isPresent(e)
        || JavaxPatternPrism.isPresent(e)
        || AvajeEmailPrism.isPresent(e)
        || JavaxEmailPrism.isPresent(e)
        || JakartaEmailPrism.isPresent(e);
  }
}
