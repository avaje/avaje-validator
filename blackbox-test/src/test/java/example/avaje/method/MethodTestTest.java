package example.avaje.method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.avaje.inject.test.InjectTest;
import io.avaje.validation.ConstraintViolationException;
import jakarta.inject.Inject;

@InjectTest
class MethodTestTest {

  @Inject private MethodTest proxy;

  @Test
  void test() {
    assertThatNoException().isThrownBy(() -> proxy.test(List.of(""), 1, "result"));
  }

  @Test
  void invalid() {
    try {

      proxy.test(List.of(), 0, null);
      fail("how???");
    } catch (final ConstraintViolationException e) {
      final var violations = e.violations();

      assertThat(violations).hasSize(3);
    }
  }
}
