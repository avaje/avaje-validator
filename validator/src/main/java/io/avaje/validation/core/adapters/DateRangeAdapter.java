package io.avaje.validation.core.adapters;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;

final class DateRangeAdapter extends AbstractConstraintAdapter<Object> {

  private final Clock referenceClock;
  private final Duration tolerance;
  private final String _type;

  private final TemporalAmount min;
  private final TemporalAmount max;

  DateRangeAdapter(AdapterCreateRequest request, Clock referenceClock, Duration tolerance) {
    super(request);
    this.referenceClock = referenceClock;
    this.tolerance = tolerance;
    this._type = request.targetType();
    min = parsePeriod((String) request.attribute("min"), true);
    max = parsePeriod((String) request.attribute("max"), false);
  }

  private TemporalAmount parsePeriod(String period, boolean negateTolerance) {
    if (period == null || period.isEmpty()) {
      return null;
    }
    if (period.equals("now")) {
      return nowTolerance(negateTolerance);
    }
    try {
      return Period.parse(period);
    } catch (DateTimeParseException e) {
      return Duration.parse(period);
    }
  }

  private TemporalAmount nowTolerance(boolean negateTolerance) {
    return switch (_type) {
      case "Temporal.Instant",
        "Temporal.LocalDateTime",
        "Temporal.ZonedDateTime",
        "Temporal.OffsetDateTime" -> negateTolerance ? tolerance.negated() : tolerance;

      default -> Period.ZERO;
    };
  }

  @Override
  protected boolean isValid(Object value) {
    if (value == null) {
      return true;
    }
    return switch (_type) {
      case "Temporal.Instant" -> compare((Instant) value);
      case "Temporal.LocalDate" -> compare((LocalDate) value);
      case "Temporal.LocalDateTime" -> compare((LocalDateTime) value);
      case "Temporal.LocalTime" -> compare((LocalTime) value);
      case "Temporal.ZonedDateTime" -> compare((ZonedDateTime) value);
      case "Temporal.OffsetDateTime" -> compare((OffsetDateTime) value);
      case "Temporal.OffsetTime" -> compare((OffsetTime) value);
      case "Temporal.Year" -> compare((Year) value);
      case "Temporal.YearMonth" -> compare((YearMonth) value);
      default -> throw new IllegalStateException("Unsupported type " + _type);
    };
  }

  private boolean compare(LocalDate value) {
    LocalDate now = LocalDate.now(referenceClock);
    if (min != null && now.plus(min).isAfter(value)) {
      return false;
    }
    return max == null || !now.plus(max).isBefore(value);
  }

  private boolean compare(LocalDateTime value) {
    LocalDateTime now = LocalDateTime.now(referenceClock);
    if (min != null && now.plus(min).isAfter(value)) {
      return false;
    }
    return max == null || !now.plus(max).isBefore(value);
  }

  private boolean compare(LocalTime value) {
    LocalTime now = LocalTime.now(referenceClock);
    if (min != null && now.plus(min).isAfter(value)) {
      return false;
    }
    return max == null || !now.plus(max).isBefore(value);
  }

  private boolean compare(Instant value) {
    Instant now = Instant.now(referenceClock);
    if (min != null && now.plus(min).isAfter(value)) {
      return false;
    }
    return max == null || !now.plus(max).isBefore(value);
  }

  private boolean compare(ZonedDateTime value) {
    ZonedDateTime now = ZonedDateTime.now(referenceClock);
    if (min != null && now.plus(min).isAfter(value)) {
      return false;
    }
    return max == null || !now.plus(max).isBefore(value);
  }

  private boolean compare(OffsetDateTime value) {
    OffsetDateTime now = OffsetDateTime.now(referenceClock);
    if (min != null && now.plus(min).isAfter(value)) {
      return false;
    }
    return max == null || !now.plus(max).isBefore(value);
  }

  private boolean compare(OffsetTime value) {
    OffsetTime now = OffsetTime.now(referenceClock);
    if (min != null && now.plus(min).isAfter(value)) {
      return false;
    }
    return max == null || !now.plus(max).isBefore(value);
  }

  private boolean compare(Year value) {
    Year now = Year.now(referenceClock);
    if (min != null && now.plus(min).isAfter(value)) {
      return false;
    }
    return max == null || !now.plus(max).isBefore(value);
  }

  private boolean compare(YearMonth value) {
    YearMonth now = YearMonth.now(referenceClock);
    if (min != null && now.plus(min).isAfter(value)) {
      return false;
    }
    return max == null || !now.plus(max).isBefore(value);
  }
}
