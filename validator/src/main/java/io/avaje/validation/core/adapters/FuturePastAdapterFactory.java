package io.avaje.validation.core.adapters;

import java.lang.annotation.Annotation;
import java.time.Clock;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationContext.AnnotationFactory;

public final class FuturePastAdapterFactory implements AnnotationFactory {

  private Clock pastClock;

  private Clock futureClock;

  private final Supplier<Clock> clockSupplier;
  private final Duration tolerance;

  public FuturePastAdapterFactory(Supplier<Clock> clockSupplier, Duration tolerance) {
    this.clockSupplier = clockSupplier;
    this.tolerance = tolerance;
  }

  @Override
  public ValidationAdapter<?> create(
      Class<? extends Annotation> annotationType,
      ValidationContext context,
      Map<String, Object> attributes) {
    return switch (annotationType.getSimpleName()) {
      case "Past" -> new FuturePastAdapter(context.message(attributes), true, false, pastClock());
      case "PastOrPresent" -> new FuturePastAdapter(
          context.message(attributes), true, true, pastClock());
      case "Future" -> new FuturePastAdapter(
          context.message(attributes), false, false, futureClock());
      case "FutureOrPresent" -> new FuturePastAdapter(
          context.message(attributes), false, true, futureClock());
      default -> null;
    };
  }

  private Clock pastClock() {

    if (pastClock == null) {
      pastClock = Clock.offset(clockSupplier.get(), tolerance);
    }

    return pastClock;
  }

  private Clock futureClock() {
    if (futureClock == null) {
      futureClock = Clock.offset(clockSupplier.get(), tolerance.negated());
    }
    return futureClock;
  }
}