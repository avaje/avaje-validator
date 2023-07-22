package io.avaje.validation.core.adapters;

import java.time.Clock;
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
import java.util.Set;
import java.util.function.Function;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext;

final class FuturePastAdapter extends AbstractConstraintAdapter<Object> {

  private final boolean past;
  private final boolean includePresent;
  private final Clock referenceClock;

  FuturePastAdapter(
      ValidationContext.Message message,
      Set<Class<?>> groups,
      boolean past,
      boolean includePresent,
      Clock referenceClock) {
    super(message, groups);
    this.past = past;
    this.includePresent = includePresent;
    this.referenceClock = referenceClock;
  }

  @Override
  public boolean isValid(Object obj) {
    if (obj == null) {
      return true;
    }
    final var result =
        obj instanceof final Date date && compare(date.getTime(), Clock::millis)
            || obj instanceof final TemporalAccessor temporalAccessor
                && (temporalAccessor instanceof final Instant ins && compare(ins, Instant::now)
                    || temporalAccessor instanceof final LocalDate ld && compare(ld, LocalDate::now)
                    || temporalAccessor instanceof final LocalDateTime ldt
                        && compare(ldt, LocalDateTime::now)
                    || temporalAccessor instanceof final LocalTime lt && compare(lt, LocalTime::now)
                    || temporalAccessor instanceof final ZonedDateTime zdt
                        && compare(zdt, ZonedDateTime::now)
                    || temporalAccessor instanceof final OffsetDateTime odt
                        && compare(odt, OffsetDateTime::now)
                    || temporalAccessor instanceof final OffsetTime ot
                        && compare(ot, OffsetTime::now)
                    || temporalAccessor instanceof final Year y && compare(y, Year::now)
                    || temporalAccessor instanceof final YearMonth ym
                        && compare(ym, YearMonth::now));

    return !result;
  }

  private <T> boolean compare(Comparable<T> instant, Function<Clock, T> nowFunction) {

    final var now = nowFunction.apply(referenceClock);

    final var result = instant.compareTo(now);

    if (includePresent && result == 0) return false;

    if (result == 0) return true;

    return past ? result > 0 : result < 0;
  }
}
