package example.avaje.daterange;


import io.avaje.validation.constraints.DateRange;
import io.avaje.validation.constraints.Valid;

import java.time.LocalDate;
import java.time.YearMonth;

@Valid
public record DateRangeNow(
  @DateRange(min = "now", max = "P3M") LocalDate shipDate,
  @DateRange(min = "-P120Y", max = "-P3Y") YearMonth yearOfBirth
) {
}
