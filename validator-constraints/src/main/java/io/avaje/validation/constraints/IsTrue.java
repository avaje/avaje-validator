package io.avaje.validation.constraints;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(IsTrue.List.class)
public @interface IsTrue {
    String message() default "{avaje.validation.constraints.IsTrue.message}";

    Class<?>[] groups() default {};

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        IsTrue[] value();
    }
}
