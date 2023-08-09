package io.avaje.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element has to be in the appropriate date or temporal range.
 * <p>
 * Can be applied to java time types LocalDate, LocalTime, LocalDateTime, Instant,
 * OffsetDateTime, OffsetTime, ZonedDateTime, Year, YearMonth.
 * <p>
 * The Period can be defined as a valid Period or Duration.
 *
 * <h4>Example Periods</h4>
 * <pre>
 *
 *   "P1Y2M3D"    as 1 year, 2 months, 3 days
 *   "P4W"        as 4 weeks
 *   "P5D"        as 5 days
 *   "-P5D"       as minus 5 days
 *
 * </pre>
 *
 * <h4>Example Durations</h4>
 * <pre>
 *
 *   "PT10H"      as 10 hours
 *   "-PT6H30M"   as minus (6 hours and 30 minutes)"
 *   "P2DT3H4M"   as 2 days, 3 hours and 4 minutes
 *
 * </pre>
 *
 * @see java.time.Period#parse(CharSequence)
 * @see java.time.Duration#parse(CharSequence)
 */
@Constraint(unboxPrimitives = true)
@Documented
@Retention(RUNTIME)
@Repeatable(DateRange.List.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
public @interface DateRange {

  /** Period or Duration for the lower limit of now + min (often a negative period) */
  String min() default "";

  /** Period or Duration for the upper limit of now + max */
  String max() default "";

  String message() default "{avaje.DateRange.message}";

  Class<?>[] groups() default {};

  /** Defines several {@code @DateRange} annotations on the same element. */
  @Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
  @Retention(RUNTIME)
  @Documented
  @interface List {
    DateRange[] value();
  }
}
