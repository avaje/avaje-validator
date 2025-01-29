package example.avaje.repeat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

public class PatternListTest {

  @Valid
  public static class Bean {

    @Pattern(regexp = "^[ABCD]{4}$", message = "Missing ABCD")
    @Pattern(regexp = ".*[A].*", message = "Missing A")
    @Pattern(regexp = ".*[B].*", message = "Missing B")
    @Pattern(regexp = ".*[C].*", message = "Missing C")
    @Pattern(regexp = ".*[D].*", message = "Missing D")
    public String field;
  }

  private final Validator validator =
    Validator.builder()
      .build();

  @Test
  void testNoViolations() {
    final Bean bean = new Bean();
    bean.field = "ABCD";

    validator.validate(bean);
    // no exception
  }

  @Test
  void testMultipleViolations() {
    final Bean bean = new Bean();
    bean.field = "f";

    final ConstraintViolationException exceptions =
      assertThrows(ConstraintViolationException.class, () -> validator.validate(bean));

    for (final ConstraintViolation violation : exceptions.violations()) {
      System.out.println("Violation: " + violation.toString());
    }

    assertEquals(5, exceptions.violations().size());
  }
}
