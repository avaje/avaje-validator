package io.avaje.validation.core.adapters;

import java.time.Clock;
import java.time.Duration;
import java.util.function.Supplier;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;
import io.avaje.validation.adapter.ValidationContext.AnnotationFactory;

public final class FuturePastAdapterFactory implements AnnotationFactory {

  private final Supplier<Clock> clockSupplier;
  private final Duration tolerance;

  private Clock pastClock;
  private Clock futureClock;

  public FuturePastAdapterFactory(Supplier<Clock> clockSupplier, Duration tolerance) {
    this.clockSupplier = clockSupplier;
    this.tolerance = tolerance;
  }

  @Override
  public ValidationAdapter<?> create(AdapterCreateRequest request) {
    return switch (request.annotationType().getSimpleName()) {
      case "Past" -> new FuturePastAdapter(request, true, false, pastClock());
      case "PastOrPresent" -> new FuturePastAdapter(request, true, true, pastClock());
      case "Future" -> new FuturePastAdapter(request, false, false, futureClock());
      case "FutureOrPresent" -> new FuturePastAdapter(request, false, true, futureClock());
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
