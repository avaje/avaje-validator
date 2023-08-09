package example.avaje.daterange;


import io.avaje.validation.constraints.DateRange;
import io.avaje.validation.constraints.Valid;

import java.time.Instant;
import java.time.LocalDate;

@Valid
public record DateRangeLocal(
  @DateRange(min = "-P2D", max = "P2D") LocalDate value,
  @DateRange(min = "-P2D", max = "P2D") Instant instant
) {
}
