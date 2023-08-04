package example.avaje.range;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;

class AMinTest {

  Validator validator = Validator.builder().build();

  @Test
  void validNull() {
    validator.validate(new AMinCheck(null, null, null, null));
  }

  @Test
  void valid() {
    validator.validate(new AMinCheck(4d, 4f, new BigDecimal("4.0"), new BigInteger("4")));
  }

  @Test
  void invalidMin() {
    var violations = new ArrayList<>(validator.check(new AMinCheck(3.9d, 3.9f, new BigDecimal("3.9"), new BigInteger("3"))));
    assertThat(violations).hasSize(4);
    for (ConstraintViolation violation : violations) {
      assertThat(violation.message()).isEqualTo("must be greater than or equal to 4");
    }
  }
}
