package example.avaje.daterange;


import io.avaje.validation.constraints.DateRange;
import io.avaje.validation.constraints.Valid;

import java.time.*;

/**
 * These types do not use the configured tolerance.
 */
@Valid
public record DateRangeNowTolerance0(
  @DateRange(min = "now", max = "now") LocalDate ld,
  @DateRange(min = "now", max = "now") Year year,
  @DateRange(min = "now", max = "now") YearMonth ym
) {
}
