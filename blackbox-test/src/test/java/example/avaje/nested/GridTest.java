package example.avaje.nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;

class GridTest {

  final Validator validator = Validator.builder().build();

  @Test
  void valid() {
    validator.validate(
        new Grid(
            List.of(List.of("ok")), Map.of("owner", List.of("ok")), List.of(Map.of("key", "ok"))));
  }

  @Test
  void nestedListInList_reportsViolationWithDoubleIndexPath() {
    try {
      validator.validate(new Grid(List.of(List.of("ok", "")), Map.of(), List.of()));
      fail("expected violation");
    } catch (final ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(1);
      final var violation = e.violations().iterator().next();
      assertThat(violation.path()).isEqualTo("matrix[0][1]");
      assertThat(violation.message()).isEqualTo("must not be blank");
    }
  }

  @Test
  void nestedListInMapValue_reportsViolationWithKeyAndIndexPath() {
    try {
      validator.validate(new Grid(List.of(), Map.of("owner", List.of("ok", " ")), List.of()));
      fail("expected violation");
    } catch (final ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(1);
      final var violation = e.violations().iterator().next();
      assertThat(violation.path()).isEqualTo("tagsByOwner[owner][1]");
    }
  }

  @Test
  void nestedMapInList_reportsViolationWithIndexAndKeyPath() {
    try {
      validator.validate(new Grid(List.of(), Map.of(), List.of(Map.of("key", ""))));
      fail("expected violation");
    } catch (final ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(1);
      final var violation = e.violations().iterator().next();
      assertThat(violation.path()).isEqualTo("listOfMaps[0][key]");
    }
  }
}
