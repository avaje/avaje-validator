package example.avaje.inherit;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ConcreteWithGenericBaseTest {

  final Validator validator = Validator.builder().build();

  @Test
  void valid() {
    var bean = new ConcreteWithGenericBase(new ConcreteWithGenericBase.InnerConfig("hello", "code1"));
    validator.validate(bean);
  }

  @Test
  void cascadeValidation_blankValue() {
    var bean = new ConcreteWithGenericBase(new ConcreteWithGenericBase.InnerConfig("", "code1"));
    var violation = one(bean);
    assertThat(violation.path()).contains("value");
    assertThat(violation.message()).isEqualTo("must not be blank");
  }

  @Test
  void cascadeValidation_nullCode() {
    var bean = new ConcreteWithGenericBase(new ConcreteWithGenericBase.InnerConfig("hello", null));
    var violation = one(bean);
    assertThat(violation.path()).contains("code");
    assertThat(violation.message()).isEqualTo("must not be null");
  }

  ConstraintViolation one(Object bean) {
    try {
      validator.validate(bean);
      fail("expected violation");
      return null;
    } catch (ConstraintViolationException e) {
      List<ConstraintViolation> violations = new ArrayList<>(e.violations());
      assertThat(violations).hasSize(1);
      return violations.get(0);
    }
  }
}
