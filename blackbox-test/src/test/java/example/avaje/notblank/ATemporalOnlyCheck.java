package example.avaje.notblank;

import io.avaje.validation.constraints.Future;
import io.avaje.validation.constraints.FutureOrPresent;
import io.avaje.validation.constraints.Past;
import io.avaje.validation.constraints.Valid;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Valid
public record ATemporalOnlyCheck
  (
    @Past Instant prior,
    @Future OffsetDateTime after,
    @Future ZonedDateTime after2,
    @Future LocalDateTime after3,
    @Past java.util.Date utilDateOk,

    //@PastOrPresent
    String strVal
    ) {
}
