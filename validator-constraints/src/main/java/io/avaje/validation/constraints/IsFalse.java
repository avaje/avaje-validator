package io.avaje.validation.constraints;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(IsFalse.List.class)
public @interface IsFalse {
    String message() default "{avaje.validation.constraints.IsFalse.message}";

    Class<?>[] groups() default {};

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        IsFalse[] value();
    }
}
