package io.avaje.validation;

public @interface SubTypes {

  Class<?>[] value() default {};
}
