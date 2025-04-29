package example.avaje.clock;

import java.time.LocalDate;

import io.avaje.validation.constraints.Future;
import io.avaje.validation.constraints.Valid;

@Valid
public record Clocky(@Future LocalDate startDate) {}
