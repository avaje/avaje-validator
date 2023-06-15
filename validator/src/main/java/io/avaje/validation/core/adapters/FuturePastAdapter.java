package io.avaje.validation.core.adapters;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.function.Predicate;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;

final class FuturePastAdapter implements ValidationAdapter<Object> {

  private final ValidationContext.Message message;
  private final boolean past;
  private final boolean includePresent;

  FuturePastAdapter(ValidationContext.Message message, boolean past, boolean includePresent) {
    this.message = message;
    this.past = past;
    this.includePresent = includePresent;
  }

  @Override
  public boolean validate(Object obj, ValidationRequest req, String propertyName) {
    if (obj == null) {
      req.addViolation(message, propertyName);
      return false;
    }
    if (obj instanceof final Date date && compare(date)
        || obj instanceof final TemporalAccessor temporalAccessor
            && (temporalAccessor instanceof final Instant ins && compare(ins)
                || temporalAccessor instanceof final LocalDate ld && compare(ld)
                || temporalAccessor instanceof final LocalDateTime ldt && compare(ldt)
                || temporalAccessor instanceof final LocalTime lt && compare(lt)
                || temporalAccessor instanceof final ZonedDateTime zdt && compare(zdt)
                || temporalAccessor instanceof final OffsetDateTime odt && compare(odt)
                || temporalAccessor instanceof final OffsetTime ot && compare(ot)
                || temporalAccessor instanceof final Year y && compare(y)
                || temporalAccessor instanceof final YearMonth ym && compare(ym))) {
      req.addViolation(message, propertyName);
      return false;
    }
    return true;
  }

  private boolean compare(Date date) {
    Predicate<Date> predicate = past ? date::before : date::after;
    predicate = includePresent ? predicate.or(date::equals) : predicate;
    return predicate.negate().test(Date.from(Instant.now()));
  }

  private boolean compare(Instant instant) {
    Predicate<Instant> predicate = past ? instant::isBefore : instant::isAfter;
    predicate = includePresent ? predicate.or(instant::equals) : predicate;
    return predicate.negate().test(Instant.now());
  }

  private boolean compare(LocalDate instant) {
    Predicate<LocalDate> predicate = past ? instant::isBefore : instant::isAfter;
    predicate = includePresent ? predicate.or(instant::equals) : predicate;
    return predicate.negate().test(LocalDate.now());
  }

  private boolean compare(LocalDateTime instant) {
    Predicate<LocalDateTime> predicate = past ? instant::isBefore : instant::isAfter;
    predicate = includePresent ? predicate.or(instant::equals) : predicate;
    return predicate.negate().test(LocalDateTime.now());
  }

  private boolean compare(LocalTime instant) {
    Predicate<LocalTime> predicate = past ? instant::isBefore : instant::isAfter;
    predicate = includePresent ? predicate.or(instant::equals) : predicate;
    return predicate.negate().test(LocalTime.now());
  }

  private boolean compare(ZonedDateTime instant) {
    Predicate<ZonedDateTime> predicate = past ? instant::isBefore : instant::isAfter;
    predicate = includePresent ? predicate.or(instant::equals) : predicate;
    return predicate.negate().test(ZonedDateTime.now());
  }

  private boolean compare(OffsetDateTime instant) {
    Predicate<OffsetDateTime> predicate = past ? instant::isBefore : instant::isAfter;
    predicate = includePresent ? predicate.or(instant::equals) : predicate;
    return predicate.negate().test(OffsetDateTime.now());
  }

  private boolean compare(OffsetTime instant) {
    Predicate<OffsetTime> predicate = past ? instant::isBefore : instant::isAfter;
    predicate = includePresent ? predicate.or(instant::equals) : predicate;
    return predicate.negate().test(OffsetTime.now());
  }

  private boolean compare(Year instant) {
    Predicate<Year> predicate = past ? instant::isBefore : instant::isAfter;
    predicate = includePresent ? predicate.or(instant::equals) : predicate;
    return predicate.negate().test(Year.now());
  }

  private boolean compare(YearMonth instant) {
    Predicate<YearMonth> predicate = past ? instant::isBefore : instant::isAfter;
    predicate = includePresent ? predicate.or(instant::equals) : predicate;
    return predicate.negate().test(YearMonth.now());
  }
}
