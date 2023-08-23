package io.avaje.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated element must be an instant, date or time in the past.
 *
 * <p><i>Now</i> is defined by the {@link Clock} Supplier attached to the {@link Validator}. The
 * default clock defines the current time according to the virtual machine, applying the current
 * default time zone if needed.
 *
 * <p>Supported types are:
 *
 * <ul>
 *   <li>{@code java.util.Date}
 *   <li>{@code java.util.Calendar}
 *   <li>{@code java.time.Instant}
 *   <li>{@code java.time.LocalDate}
 *   <li>{@code java.time.LocalDateTime}
 *   <li>{@code java.time.LocalTime}
 *   <li>{@code java.time.MonthDay}
 *   <li>{@code java.time.OffsetDateTime}
 *   <li>{@code java.time.OffsetTime}
 *   <li>{@code java.time.Year}
 *   <li>{@code java.time.YearMonth}
 *   <li>{@code java.time.ZonedDateTime}
 * </ul>
 *
 * <p>{@code null} elements are considered valid.
 */
@Constraint
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface Past {

  String message() default "{avaje.Past.message}";

  Class<?>[] groups() default {};
}
