package io.avaje.validation.generator;

import javax.lang.model.element.Element;

import io.avaje.prism.GeneratePrism;

@GeneratePrism(
    value = io.avaje.validation.constraints.Constraint.class,
    name = "AvajeConstraintPrism",
    superInterfaces = ConstraintPrism.class)
@GeneratePrism(
    value = jakarta.validation.Constraint.class,
    name = "JakartaConstraintPrism",
    superInterfaces = ConstraintPrism.class)
@GeneratePrism(
    value = javax.validation.Constraint.class,
    name = "JavaxConstraintPrism",
    superInterfaces = ConstraintPrism.class)
public interface ConstraintPrism {

  static boolean isPresent(Element e) {
    return AvajeConstraintPrism.isPresent(e)
        || JakartaConstraintPrism.isPresent(e)
        || JavaxConstraintPrism.isPresent(e);
  }
}
