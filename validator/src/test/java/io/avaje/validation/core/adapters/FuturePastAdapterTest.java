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
  void testNull() {
    assertThat(pastAdapter.validate(null, request)).isTrue();
    assertThat(pastOrPresentAdapter.validate(null, request)).isTrue();
    assertThat(futureAdapter.validate(null, request)).isTrue();
    assertThat(futureOrPresentAdapter.validate(null, request)).isTrue();
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
   }
}
