package io.avaje.validation.constraints;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Digits.List.class)
public @interface Digits {
    String message() default "{avaje.validation.constraints.Digits.message}";

    Class<?>[] groups() default {};

    int value();

    int fraction() default 0;

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        Digits[] value();
    }
}
