package example.avaje.method2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import io.avaje.inject.test.InjectTest;
import io.avaje.validation.ConstraintViolationException;
import jakarta.inject.Inject;

/**
 * two @ValidMethod methods with the same simple name (example.avaje.method.MethodTest#test and
 * example.avaje.method2.MethodTest#test) living in different packages must both get a generated
 * ParamProvider rather than one silently overwriting the other.
 */
@InjectTest
class DoubleMethodTest {

  @Inject private MethodTest proxy;

  @Test
  void test() {
    assertThatNoException().isThrownBy(() -> proxy.test("result"));
  }

  @Test
  void invalid() {
    try {
      proxy.test("");
      fail("how???");
    } catch (final ConstraintViolationException e) {
      final var violations = e.violations();

      assertThat(violations).hasSize(1);
    }
  }
}
