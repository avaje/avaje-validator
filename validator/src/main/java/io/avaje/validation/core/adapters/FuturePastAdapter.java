package io.avaje.validation.core.adapters;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext;

import java.time.*;
import java.util.Date;
import java.util.function.Function;

final class FuturePastAdapter extends AbstractConstraintAdapter<Object> {

  private final boolean past;
  private final boolean includePresent;
  private final Clock referenceClock;
  private final String _type;

  FuturePastAdapter(
    ValidationContext.AdapterCreateRequest request,
    boolean past,
    boolean includePresent,
    Clock referenceClock) {
    super(request);
    this.past = past;
    this.includePresent = includePresent;
    this.referenceClock = referenceClock;
    this._type = request.targetType();
  }

  @Override
  public boolean isValid(Object obj) {

    return switch (_type) {
      case "Temporal.Date" -> compare(((Date) obj).getTime(), Clock::millis);
      case "Temporal.Instant" -> compare((Instant) obj, Instant::now);
      case "Temporal.LocalDate" -> compare((LocalDate) obj, LocalDate::now);
      case "Temporal.LocalDateTime" -> compare((LocalDateTime) obj, LocalDateTime::now);
      case "Temporal.LocalTime" -> compare((LocalTime) obj, LocalTime::now);
      case "Temporal.ZonedDateTime" -> compare((ZonedDateTime) obj, ZonedDateTime::now);
      case "Temporal.OffsetDateTime" -> compare((OffsetDateTime) obj, OffsetDateTime::now);
      case "Temporal.OffsetTime" -> compare((OffsetTime) obj, OffsetTime::now);
      case "Temporal.Year" -> compare((Year) obj, Year::now);
      case "Temporal.YearMonth" -> compare((YearMonth) obj, YearMonth::now);
      default -> throw new IllegalStateException("Unsupported type " + _type);
    };
  }

  private <T> boolean compare(Comparable<T> instant, Function<Clock, T> nowFunction) {
    final var now = nowFunction.apply(referenceClock);
    final var result = instant.compareTo(now);

    if (includePresent && result == 0) {
      return true;
    } else if (result == 0) {
      return false;
    } else {
      return past ? result < 0 : result > 0;
    }
  }
}
