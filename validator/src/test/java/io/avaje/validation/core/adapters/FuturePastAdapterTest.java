package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.core.BasicTest;

class FuturePastAdapterTest extends BasicTest {

  @interface Past {}

  @interface Future {}

  @interface PastOrPresent {}

  @interface FutureOrPresent {}

  ValidationAdapter<Object> pastAdapter = ctx.adapter(Past.class, Map.of("message", "wibbly"));
  ValidationAdapter<Object> pastOrPresentAdapter =
      ctx.adapter(PastOrPresent.class, Map.of("message", "wobbly"));
  ValidationAdapter<Object> futureAdapter = ctx.adapter(Future.class, Map.of("message", "timey"));
  ValidationAdapter<Object> futureOrPresentAdapter =
      ctx.adapter(FutureOrPresent.class, Map.of("message", "wimey"));

  @Test
  void testNull() {
    assertThat(pastAdapter.validate(null, request)).isTrue();
    assertThat(pastOrPresentAdapter.validate(null, request)).isTrue();
    assertThat(futureAdapter.validate(null, request)).isTrue();
    assertThat(futureOrPresentAdapter.validate(null, request)).isTrue();
  }

  @Test
  void testPast() {

    Object value;
    final var inst = Instant.now().minusMillis(1);

    // Instant
    assertPast(inst);

    // date
    value = Date.from(inst);

    assertPast(value);
    // LocalDate
    value = LocalDate.now().minusDays(1);

    assertPast(value);
    // LocalDateTime
    value = LocalDateTime.now().minusDays(1);

    assertPast(value);
    // LocalTime
    value = LocalTime.now().minusHours(1);

    assertPast(value);
    // ZonedDateTime
    value = ZonedDateTime.now().minusHours(1);

    assertPast(value);
    // OffsetDateTime
    value = OffsetDateTime.now().minusHours(1);

    assertPast(value);
    // OffsetTime
    value = OffsetTime.now().minusHours(1);

    assertPast(value);
    // Year
    value = Year.now().minusYears(1);

    assertPast(value);
    // YearMonth
    value = YearMonth.now().minusYears(1);
    assertPast(value);
  }

  @Test
  void testFuture() {

    Object value;
    final var inst = Instant.now().plusMillis(1234567890);

    // Instant
    assertFuture(inst);

    // date
    value = Date.from(inst);

    assertFuture(value);

    // LocalDate
    value = LocalDate.now().plusDays(1);

    assertFuture(value);

    // LocalDateTime
    value = LocalDateTime.now().plusDays(1);

    assertFuture(value);
    // LocalTime
    value = LocalTime.now().plusMinutes(1);

    assertFuture(value);
    // ZonedDateTime
    value = ZonedDateTime.now().plusHours(1);

    assertFuture(value);
    // OffsetDateTime
    value = OffsetDateTime.now().plusHours(1);

    assertFuture(value);
    // OffsetTime
    value = OffsetTime.now().plusMinutes(1);

    assertFuture(value);
    // Year
    value = Year.now().plusYears(1);

    assertFuture(value);
    // YearMonth
    value = YearMonth.now().plusYears(1);
    assertFuture(value);
  }

  @Test
  void testPresent() {

    Object value;
    final var inst = Instant.now();

    // Instant is too precise for a present test

    // date
    value = Date.from(inst);

    assertPresent(value);

    // LocalDate
    value = LocalDate.now();

    assertPresent(value);

    // LocalDateTime
    value = LocalDateTime.now();

    assertPresent(value);
    // LocalTime
    value = LocalTime.now();

    assertPresent(value);

    // ZDT is too precise for a present test

    // OffsetDateTime
    value = OffsetDateTime.now();

    assertPresent(value);
    // OffsetTime
    value = OffsetTime.now();

    assertPresent(value);
    // Year
    value = Year.now();

    assertPresent(value);
    // YearMonth
    value = YearMonth.now();
    assertPresent(value);
  }

  private void assertPast(Object value) {
    assertThat(pastAdapter.validate(value, request)).isTrue();
    assertThat(pastOrPresentAdapter.validate(value, request)).isTrue();
    assertThat(futureAdapter.validate(value, request)).isFalse();
    assertThat(futureOrPresentAdapter.validate(value, request)).isFalse();
  }

  private void assertFuture(Object value) {
    assertThat(pastAdapter.validate(value, request)).isFalse();
    assertThat(pastOrPresentAdapter.validate(value, request)).isFalse();
    assertThat(futureAdapter.validate(value, request)).isTrue();
    assertThat(futureOrPresentAdapter.validate(value, request)).isTrue();
  }

  private void assertPresent(Object value) {
    assertThat(pastOrPresentAdapter.validate(value, request)).isTrue();
    assertThat(futureOrPresentAdapter.validate(value, request)).isTrue();
    assertThat(pastAdapter.validate(value, request)).isFalse();
    assertThat(futureAdapter.validate(value, request)).isFalse();
  }
}
