package example.avaje.optional;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import example.avaje.composable.Sans;
import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

class OptionalTest {

  final Validator validator = Validator.builder().build();

  @Test
  void valid() {
    final var monarch =
        new CurseBearer(
            Optional.of("Belmont"),
            OptionalInt.of(5),
            OptionalLong.of(10000),
            OptionalDouble.of(42.0));
    validator.validate(monarch);
  }

  @Test
  void validEmpty() {
    final var hollow =
        new CurseBearer(
            Optional.empty(), OptionalInt.empty(), OptionalLong.empty(), OptionalDouble.empty());
    validator.validate(hollow);
  }

  @Test
  void validNull() {
    final var hollow = new CurseBearer(null, null, null, null);
    validator.validate(hollow);
  }

  @Test
  void invalid() {
    final var violations =
        violations(
            new CurseBearer(
                Optional.of(""), OptionalInt.of(0), OptionalLong.of(0), OptionalDouble.of(0)));
    assertThat(violations)
        .contains(
            "it'll happen to you too",
            "must be greater than 0",
            "You Died",
            "you didn't pass the vigor check");
  }

  Set<String> violations(Object any) {
    try {
      validator.validate(any);
      fail("not expected");
      return null;
    } catch (final ConstraintViolationException e) {
      return e.violations().stream().map(ConstraintViolation::message).collect(toSet());
    }
  }
}
