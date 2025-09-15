package io.avaje.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * The annotated element must be an instant, date or time in the past or in the present.
 *
 * <p><i>Now</i> is defined by the {@code Clock} Supplier attached to the {@code Validator}. The
 * default clock defines the current time according to the virtual machine, applying the current
 * default time zone if needed.
 *
 * <p>The notion of present is defined relatively to the type on which the constraint is used. For
 * instance, if the constraint is on a {@code Year}, present would mean the whole current year.
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
 *
 * @author Guillaume Smet
 */
@Constraint
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface PastOrPresent {

  String message() default "{avaje.PastOrPresent.message}";

  Class<?>[] groups() default {};
}
