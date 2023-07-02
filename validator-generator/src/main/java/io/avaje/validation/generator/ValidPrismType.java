package io.avaje.validation.generator;

import javax.lang.model.element.Element;

public interface ValidPrismType {

  static boolean isPresent(Element e) {
    return ValidPojoPrism.isPresent(e)
        || JakartaValidPrism.isPresent(e)
        || JavaxValidPrism.isPresent(e)
        || ValidPrism.isPresent(e);
  }
}
