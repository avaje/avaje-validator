package example.avaje.method;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    assertThatNoException().isThrownBy(() -> proxy.test(List.of(""), 1, null));
  }

  @Test
  void invalid() {
    assertThatThrownBy(() -> proxy.test(null, 0, null))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
