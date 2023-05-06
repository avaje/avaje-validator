package io.avaje.validation.constraints;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AssertFalse.List.class)
public @interface AssertFalse {
    String message() default "{avaje.validation.constraints.AssertFalse.message}";

    Class<?>[] groups() default {};

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        AssertFalse[] value();
    }
}
