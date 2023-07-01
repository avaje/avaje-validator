package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.avaje.validation.Constraint;

@Constraint
@Target({METHOD, FIELD, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Min.List.class)
public @interface Min {
    String message() default "{avaje.Min.message}";

    Class<?>[] groups() default {};

    long value();

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Min[] value();
    }
}
