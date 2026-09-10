package example.avaje.payload;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class PayloadTest {

  final Validator validator = Validator.builder().build();

  @Test
  void noPayload() {
    assertThat(validator.check(new PayloadBean("pay"))).isEmpty();
  }

  @Test
  void carriesPayload() {
    var violation = validator.check(new PayloadBean("")).iterator().next();
    assertThat(violation.payload()).containsExactly(PayloadBean.Severity.Error.class);
  }

  @Test
  void jakartaPayload() {
    var violation = validator.check(new JakartaPayload("")).iterator().next();
    assertThat(violation.payload()).containsExactly(JakartaPayload.Severity.Error.class);
  }
}
