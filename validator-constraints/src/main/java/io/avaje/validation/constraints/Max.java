package io.avaje.validation.constraints;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Max.List.class)
public @interface Max {
    String message() default "{avaje.validation.constraints.Max.message}";

    Class<?>[] groups() default {};

    double value();

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Max[] value();
    }
}
