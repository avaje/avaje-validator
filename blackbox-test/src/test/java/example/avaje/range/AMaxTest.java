package example.avaje.range;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;

class AMaxTest {

  Validator validator = Validator.builder().build();

  @Test
  void validNull() {
    validator.validate(new AMaxCheck(null, null, null, null));
  }

  @Test
  void valid() {
    validator.validate(new AMaxCheck(1d, 1f, ONE, BigInteger.TWO));
  }

  @Test
  void invalidMax() {
    var violations = new ArrayList<>(validator.check(new AMaxCheck(5d, 5f, TEN, BigInteger.TEN)));
    assertThat(violations).hasSize(4);
    for (ConstraintViolation violation : violations) {
      assertThat(violation.message()).isEqualTo("must be less than or equal to 4");
    }
  }
}
