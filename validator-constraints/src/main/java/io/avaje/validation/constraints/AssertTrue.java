package io.avaje.validation.constraints;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AssertTrue.List.class)
public @interface AssertTrue {
    String message() default "{avaje.validation.constraints.AssertTrue.message}";

    Class<?>[] groups() default {};

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        AssertTrue[] value();
    }
}
