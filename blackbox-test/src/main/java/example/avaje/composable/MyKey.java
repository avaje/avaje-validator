package example.avaje.composable;

import io.avaje.validation.constraints.Constraint;
import io.avaje.validation.constraints.Pattern;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Pattern(regexp = "[A-Z0-9_]{2,8}")
@Constraint
@Retention(SOURCE)
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
public @interface MyKey {

  String message() default "{example.avaje.MyKey.message}";

  Class<?>[] groups() default {};
}
