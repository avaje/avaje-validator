package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target({METHOD, FIELD, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(PastOrPresent.List.class)
public @interface PastOrPresent {

  String message() default "{avaje.PastOrPresent.message}";

  Class<?>[] groups() default {};

  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  @Documented
  @interface List {
    PastOrPresent[] value();
  }
}
