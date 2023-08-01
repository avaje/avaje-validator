package example.avaje.composable;

import io.avaje.validation.constraints.Constraint;
import io.avaje.validation.constraints.Length;
import io.avaje.validation.constraints.Pattern;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Pattern(regexp = "[A-Z]+")
@Length(max = 5)
@Constraint
@Retention(SOURCE)
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
public @interface MySerial {

  String message() default "{example.avaje.MySerial.message}";

  Class<?>[] groups() default {};
}
