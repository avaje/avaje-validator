package io.avaje.validation.constraints;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Size.List.class)
public @interface Size {
    String message() default "{avaje.validation.constraints.Size.message}";

    Class<?>[] groups() default {};

    int min();

    int max();

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Size[] value();
    }
}
