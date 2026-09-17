package example.jakarta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

class JNestedMinTest {

  final Validator validator = Validator.builder().build();

  @Test
  void valid() {
    validator.validate(new JNestedMin());
  }

  @Test
  void nullAndEmptyValues_areValid() {
    final var bean = new JNestedMin();

    bean.fieldValues = null;
    validator.validate(bean);

    bean.fieldValues = List.of();
    validator.validate(bean);

    bean.fieldValues = Arrays.asList(1L, null);
    validator.validate(bean);
  }

  @Test
  void fieldElement_reportsViolation() {
    final var bean = new JNestedMin();
    bean.fieldValues = List.of(0L);

    try {
      validator.validate(bean);
      fail("expected violation");
    } catch (final ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(1);
      final var violation = e.violations().iterator().next();
      assertThat(violation.path()).isEqualTo("fieldValues[0]");
      assertThat(violation.message()).isEqualTo("must be greater than or equal to 1");
    }
  }

  @Test
  void multipleFieldElements_reportEachViolation() {
    final var bean = new JNestedMin();
    bean.fieldValues = List.of(0L, -1L, 1L);

    try {
      validator.validate(bean);
      fail("expected violation");
    } catch (final ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(2);
      assertThat(e.violations()).extracting("path")
          .containsExactly("fieldValues[0]", "fieldValues[1]");
    }
  }

  @Test
  void getterElement_reportsViolation() {
    final var bean = new JNestedMinGetter();
    bean.setValues(List.of(0L));

    try {
      validator.validate(bean);
      fail("expected violation");
    } catch (final ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(1);
      final var violation = e.violations().iterator().next();
      assertThat(violation.path()).isEqualTo("values[0]");
      assertThat(violation.message()).isEqualTo("must be greater than or equal to 1");
    }
  }
}
