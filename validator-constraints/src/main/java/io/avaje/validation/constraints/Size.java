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
@Repeatable(Size.List.class)
public @interface Size {
    String message() default "{avaje.Size.message}";

    Class<?>[] groups() default {};

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Size[] value();
    }
}
