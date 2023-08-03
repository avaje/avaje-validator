package io.avaje.validation.generator;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public interface ValidPrism {

  static boolean isPresent(Element e) {
    return AvajeValidPrism.isPresent(e)
        || JakartaValidPrism.isPresent(e)
        || JavaxValidPrism.isPresent(e)
        || HttpValidPrism.isPresent(e);
  }

  static boolean isInstance(AnnotationMirror e) {
    return AvajeValidPrism.getInstance(e) != null
        || JakartaValidPrism.getInstance(e) != null
        || JavaxValidPrism.getInstance(e) != null
        || HttpValidPrism.getInstance(e) != null;
  }
}
