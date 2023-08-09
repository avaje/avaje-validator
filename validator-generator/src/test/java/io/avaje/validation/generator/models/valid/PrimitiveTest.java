package io.avaje.validation.generator.models.valid;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.validation.constraints.Constraint;

@Retention(SOURCE)
@Target(FIELD)
@Constraint(unboxPrimitives = true)
public @interface PrimitiveTest {}
