package example.avaje.daterange;


import io.avaje.validation.constraints.DateRange;
import io.avaje.validation.constraints.Valid;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * These types use the configured tolerance when using 'now'.
 */
@Valid
public record DateRangeNowTolerance(
  @DateRange(min = "now", max = "now") Instant instant,
  @DateRange(min = "now", max = "now") LocalDateTime ldt,
  @DateRange(min = "now", max = "now") OffsetDateTime odt,
  @DateRange(min = "now", max = "now") ZonedDateTime zdt
) {
}
