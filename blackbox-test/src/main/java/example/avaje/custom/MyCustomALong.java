package example.avaje.custom;

import io.avaje.validation.constraints.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@Target(FIELD)
@Constraint(unboxPrimitives = true)
public @interface MyCustomALong {
  String message() default "{org.foo.MyCustomALong.message}";

}
