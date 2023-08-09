package example.avaje.range;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class APrimitiveTest {

  Validator validator = Validator.builder().build();

  byte byte0 = 0x0;
  byte byte1 = 0x1;
  byte byte2 = 0x2;
  byte byte3 = 0x3;
  byte byte4 = 0x4;
  short short0 = 0;
  short short1 = 1;
  short short2 = 2;
  short short3 = 3;
  short short4 = 4;

  @Test
  void rangeLongValid() {
    validator.validate(new APrimitiveLongRange(1));
    validator.validate(new APrimitiveLongRange(2));
    validator.validate(new APrimitiveLongRange(3));
    validator.validate(new APrimitiveRange(byte1, short1, 1, 1, 1d, 1f));
    validator.validate(new APrimitiveRange(byte1, short1, 1, 1, 1.0d, 1.0f));
    validator.validate(new APrimitiveRange(byte1, short1, 1, 1, 1.1d, 1.1f));
    validator.validate(new APrimitiveRange(byte3, short3, 3, 3, 3d, 3f));
    validator.validate(new APrimitiveRange(byte3, short3, 3, 3, 3.0d, 3.0f));
    validator.validate(new APrimitiveRange(byte3, short3, 3, 3, 2.9d, 2.9f));
    validator.validate(new APrimitiveRange(byte3, short3, 3, 3, 2.9d, 2.9f));
    validator.validate(new APrimitiveMax(byte3, short3, 3, 3, 3.0d, 3.0f));
    validator.validate(new APrimitiveMax(byte3, short3, 3, 3, 2.9d, 2.9f));
    validator.validate(new APrimitiveMin(byte3, short3, 3, 3, 3.0d, 3.0f));
    validator.validate(new APrimitiveMin(byte3, short3, 3, 3, 3.01d, 3.01f));
  }

  @Test
  void rangeLongBelowMin() {
    var violations  = new ArrayList<>(validator.check(new APrimitiveLongRange(0)));
    assertThat(violations).hasSize(1);
    assertThat(violations.get(0).message()).isEqualTo("must be between 1 and 3");
  }

  @Test
  void rangeBelowMin() {
    var violations  = new ArrayList<>(validator.check(new APrimitiveRange(byte0, short0, 0, 0, 0.9d, 0.9f)));
    assertThat(violations).hasSize(6);
    violations.stream()
      .map(ConstraintViolation::message)
      .forEach(msg -> assertThat(msg).isEqualTo("must be between 1 and 3"));
  }

  @Test
  void rangeLongAboveMax() {
    var violations  = new ArrayList<>(validator.check(new APrimitiveLongRange(4)));
    assertThat(violations).hasSize(1);
    assertThat(violations.get(0).message()).isEqualTo("must be between 1 and 3");
  }

  @Test
  void rangeAboveMax() {
    var violations  = new ArrayList<>(validator.check(new APrimitiveRange(byte4, short4, 4, 4, 3.1d, 3.1f)));
    assertThat(violations).hasSize(6);
    violations.stream()
      .map(ConstraintViolation::message)
      .forEach(msg -> assertThat(msg).isEqualTo("must be between 1 and 3"));
  }

  @Test
  void maxAboveMax() {
    var violations  = new ArrayList<>(validator.check(new APrimitiveMax(byte4, short4, 4, 4, 3.1d, 3.1f)));
    assertThat(violations).hasSize(6);
    violations.stream()
      .map(ConstraintViolation::message)
      .forEach(msg -> assertThat(msg).isEqualTo("must be less than or equal to 3"));
  }

  @Test
  void minBelowMin() {
    var violations  = new ArrayList<>(validator.check(new APrimitiveMin(byte2, short2, 2, 2, 2.9d, 2.9f)));
    assertThat(violations).hasSize(6);
    violations.stream()
      .map(ConstraintViolation::message)
      .forEach(msg -> assertThat(msg).isEqualTo("must be greater than or equal to 3"));
  }
}
