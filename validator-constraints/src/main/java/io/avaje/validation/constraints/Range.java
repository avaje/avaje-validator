package io.avaje.validation.constraints;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(io.avaje.validation.constraints.Range.List.class)
public @interface Range {
    String message() default "{avaje.validation.constraints.Range.message}";

    Class<?>[] groups() default {};

    double min();

    double max();

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        io.avaje.validation.constraints.Range[] value();
    }
}
