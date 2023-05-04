package io.avaje.validation.generator;

import java.util.List;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public interface PatternPrism {

  /** @return the regular expression to match */
  String regexp();

  /** @return array of {@code Flag}s considered when resolving the regular expression */
  List<String> flags();

  /** @return the error message template */
  String message();

  static Optional<PatternPrism> isInstance(AnnotationMirror e) {

    return Optional.<PatternPrism>empty()
        .or(() -> AvajePatternPrism.getOptional(e))
        .or(() -> JakartaPatternPrism.getOptional(e))
        .or(() -> JavaxPatternPrism.getOptional(e));
  }

  static boolean isPresent(Element e) {
    return AvajePatternPrism.isPresent(e)
        || JakartaPatternPrism.isPresent(e)
        || JavaxPatternPrism.isPresent(e);
  }
}
