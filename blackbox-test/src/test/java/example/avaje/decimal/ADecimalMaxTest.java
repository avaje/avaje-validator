package example.avaje.decimal;

import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;

class ADecimalMaxTest {

  Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    var violations = new ArrayList<>(validator.check(new ADecimalMax(new BigDecimal("4.5"), new BigDecimal("4.4"), new BigDecimal("4.5"), new BigDecimal("4.6"))));
    assertThat(violations).hasSize(0);
  }

  @Test
  void validNull() {
    var violations = new ArrayList<>(validator.check(new ADecimalMax(null, null, null, null)));
    assertThat(violations).hasSize(0);
  }

  @Test
  void validString() {
    var violations = new ArrayList<>(validator.check(new ADecimalStringMax("4.5", "4.4", "4.5", "4.6")));
    assertThat(violations).hasSize(0);
  }

  @Test
  void validDouble() {
    var violations = new ArrayList<>(validator.check(new ADoubleMax(4.5d, 4.4d, 4.5d, 4.6d)));
    assertThat(violations).hasSize(0);
  }

  @Test
  void validFloat() {
    var violations = new ArrayList<>(validator.check(new AFloatMax(4.5f, 4.4f, 4.5f, 4.6f)));
    assertThat(violations).hasSize(0);
  }

  @Test
  void validLong() {
    var violations = new ArrayList<>(validator.check(new ALongMax(4L, 4, 5, 5)));
    assertThat(violations).hasSize(0);
  }

  @Test
  void maxInc() {
    var violations = new ArrayList<>(validator.check(new ADecimalMax(TEN, TEN, ONE, ONE)));
    assertThat(violations).hasSize(4);
    assertThat(violations.get(0).message()).isEqualTo("must be less than or equal to 4.5");
    assertThat(violations.get(1).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(2).message()).isEqualTo("must be greater than or equal to 4.5");
    assertThat(violations.get(3).message()).isEqualTo("must be greater than 4.5");
  }

  @Test
  void maxInc_DE() {
    var violations = new ArrayList<>(validator.check(new ADecimalMax(TEN, TEN, ONE, ONE), Locale.GERMAN));
    assertThat(violations).hasSize(4);
    assertThat(violations.get(0).message()).isEqualTo("muss kleiner oder gleich 4.5 sein");
    assertThat(violations.get(1).message()).isEqualTo("muss kleiner 4.5 sein");
    assertThat(violations.get(2).message()).isEqualTo("muss größer oder gleich 4.5 sein");
    assertThat(violations.get(3).message()).isEqualTo("muss größer 4.5 sein");
  }

  @Test
  void maxIncString() {
    var violations = new ArrayList<>(validator.check(new ADecimalStringMax("4.6", "4.6", "4.4", "4.4")));
    assertThat(violations).hasSize(4);
    assertThat(violations.get(0).message()).isEqualTo("must be less than or equal to 4.5");
    assertThat(violations.get(1).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(2).message()).isEqualTo("must be greater than or equal to 4.5");
    assertThat(violations.get(3).message()).isEqualTo("must be greater than 4.5");
  }

  @Test
  void maxIncDouble() {
    var violations = new ArrayList<>(validator.check(new ADoubleMax(10d, 10d, 1d, 1d)));
    assertThat(violations).hasSize(4);
    assertThat(violations.get(0).message()).isEqualTo("must be less than or equal to 4.5");
    assertThat(violations.get(1).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(2).message()).isEqualTo("must be greater than or equal to 4.5");
    assertThat(violations.get(3).message()).isEqualTo("must be greater than 4.5");
  }

  @Test
  void maxIncFloat() {
    var violations = new ArrayList<>(validator.check(new AFloatMax(10f, 10f, 1f, 1f)));
    assertThat(violations).hasSize(4);
    assertThat(violations.get(0).message()).isEqualTo("must be less than or equal to 4.5");
    assertThat(violations.get(1).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(2).message()).isEqualTo("must be greater than or equal to 4.5");
    assertThat(violations.get(3).message()).isEqualTo("must be greater than 4.5");
  }

  @Test
  void maxIncLong() {
    var violations = new ArrayList<>(validator.check(new ALongMax(10, 10, 1, 1)));
    assertThat(violations).hasSize(4);
    assertThat(violations.get(0).message()).isEqualTo("must be less than or equal to 4.5");
    assertThat(violations.get(1).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(2).message()).isEqualTo("must be greater than or equal to 4.5");
    assertThat(violations.get(3).message()).isEqualTo("must be greater than 4.5");
  }

  @Test
  void boundary() {
    var violations = new ArrayList<>(validator.check(new ADecimalMax(new BigDecimal("4.5"), new BigDecimal("4.5"), new BigDecimal("4.5"), new BigDecimal("4.5"))));
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(0).path()).isEqualTo("maxExc");
    assertThat(violations.get(1).message()).isEqualTo("must be greater than 4.5");
    assertThat(violations.get(1).path()).isEqualTo("minExc");
  }

  @Test
  void boundaryString() {
    var violations = new ArrayList<>(validator.check(new ADecimalStringMax("4.5", "4.5", "4.5", "4.5")));
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(0).path()).isEqualTo("maxExc");
    assertThat(violations.get(1).message()).isEqualTo("must be greater than 4.5");
    assertThat(violations.get(1).path()).isEqualTo("minExc");
  }

  @Test
  void boundaryDouble() {
    var violations = new ArrayList<>(validator.check(new ADoubleMax(4.5d, 4.5d, 4.5d, 4.5d)));
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(0).path()).isEqualTo("maxExc");
    assertThat(violations.get(1).message()).isEqualTo("must be greater than 4.5");
    assertThat(violations.get(1).path()).isEqualTo("minExc");
  }

  @Test
  void boundaryFloat() {
    var violations = new ArrayList<>(validator.check(new AFloatMax(4.5f, 4.5f, 4.5f, 4.5f)));
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(0).path()).isEqualTo("maxExc");
    assertThat(violations.get(1).message()).isEqualTo("must be greater than 4.5");
    assertThat(violations.get(1).path()).isEqualTo("minExc");
  }

  @Test
  void boundaryLong() {
    var violations = new ArrayList<>(validator.check(new ALongMax(4, 5, 5, 4)));
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).message()).isEqualTo("must be less than 4.5");
    assertThat(violations.get(0).path()).isEqualTo("maxExc");
    assertThat(violations.get(1).message()).isEqualTo("must be greater than 4.5");
    assertThat(violations.get(1).path()).isEqualTo("minExc");
  }
}
