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

  ValidationAdapter<Object> pastAdapter = ctx.adapter(Past.class, Map.of("message", "wibbly", "_type", "Temporal.Instant"));
  ValidationAdapter<Object> pastOrPresentAdapter =
      ctx.adapter(PastOrPresent.class, Map.of("message", "wobbly", "_type", "Temporal.Instant"));
  ValidationAdapter<Object> futureAdapter = ctx.adapter(Future.class, Map.of("message", "timey", "_type", "Temporal.Instant"));
  ValidationAdapter<Object> futureOrPresentAdapter =
      ctx.adapter(FutureOrPresent.class, Map.of("message", "wimey", "_type", "Temporal.Instant"));

  @Test
  void continueOnInvalid_expect_false() {
    assertThat(pastAdapter.validate(Instant.now().plusSeconds(100), request, "foo")).isTrue();
    assertThat(futureAdapter.validate(Instant.now().minusSeconds(100), request, "foo")).isTrue();
  }

  @Test
  void testNull() {
    assertThat(isValid(pastAdapter, null)).isTrue();
    assertThat(isValid(pastOrPresentAdapter, null)).isTrue();
    assertThat(isValid(futureAdapter, null)).isTrue();
    assertThat(isValid(futureOrPresentAdapter, null)).isTrue();
  }

  @Test
  void testPast() {
    final var inst = Instant.now().minusMillis(50000);
    // Instant
    assertPast(inst);
  }

  @Test
  void testFuture() {

    final var inst = Instant.now().plusMillis(1234567890);

    // Instant
    assertFuture(inst);
  }

  @Test
  void testPresent() {

    final var inst = Instant.now();
    // Instant
    assertPresent(inst);
  }

  private void assertPast(Object value) {
    assertThat(isValid(pastAdapter, value)).isTrue();
    assertThat(isValid(pastOrPresentAdapter, value)).isTrue();
    assertThat(isValid(futureAdapter, value)).isFalse();
    assertThat(isValid(futureOrPresentAdapter, value)).isFalse();
  }

  private void assertFuture(Object value) {
    assertThat(isValid(pastAdapter, value)).isFalse();
    assertThat(isValid(pastOrPresentAdapter, value)).isFalse();
    assertThat(isValid(futureAdapter, value)).isTrue();
    assertThat(isValid(futureOrPresentAdapter, value)).isTrue();
  }

  private void assertPresent(Object value) {
    assertThat(isValid(pastOrPresentAdapter, value)).isTrue();
    assertThat(isValid(futureOrPresentAdapter, value)).isTrue();
   }
}
