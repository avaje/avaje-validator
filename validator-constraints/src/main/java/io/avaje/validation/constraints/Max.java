package io.avaje.validation.constraints;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Max.List.class)
public @interface Max {
    String message() default "{avaje.Max.message}";

    Class<?>[] groups() default {};

    long value();

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Max[] value();
    }
}
