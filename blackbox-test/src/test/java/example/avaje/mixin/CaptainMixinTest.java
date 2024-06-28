package example.avaje.mixin;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import example.avaje.mixin.Captain.Bankai;
import io.avaje.validation.Validator;

class CaptainMixinTest {

  Validator validator = Validator.builder().build();

  @Test
  void valid() {
    assertThatNoException().isThrownBy(() -> validator.validate(new Captain("kenpachi", null)));
  }

  @Test
  void invalidTrue() {
    var violations = new ArrayList<>(validator.check(new Captain("kenpachi", new Bankai(""))));
    violations.addAll(validator.check(new Captain(null, null)));
    assertThat(violations.get(0).message()).isEqualTo("must not be blank");
    assertThat(violations.get(1).message()).isEqualTo("must not be blank");
  }
}
