package io.avaje.validation.generator;

import java.util.List;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import io.avaje.prism.GeneratePrism;

@GeneratePrism(
    value = javax.validation.Valid.class,
    name = "JavaxValidPrism",
    superInterfaces = ValidPrism.class)
@GeneratePrism(
    value = jakarta.validation.Valid.class,
    name = "JakartaValidPrism",
    superInterfaces = ValidPrism.class)
@GeneratePrism(
    value = io.avaje.validation.constraints.Valid.class,
    name = "AvajeValidPrism",
    superInterfaces = ValidPrism.class)
@GeneratePrism(
    value = io.avaje.http.api.Valid.class,
    name = "HttpValidPrism",
    superInterfaces = ValidPrism.class)
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

  static ValidPrism instance(AnnotationMirror e) {
    return Optional.<ValidPrism>empty()
        .or(() -> AvajeValidPrism.getOptional(e))
        .or(() -> JakartaValidPrism.getOptional(e))
        .or(() -> JavaxValidPrism.getOptional(e))
        .or(() -> HttpValidPrism.getOptional(e))
        .orElse(null);
  }

  default List<TypeMirror> groups() {
    return List.of();
  }
}
