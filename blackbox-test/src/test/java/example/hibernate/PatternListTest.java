package example.hibernate;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

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
          .add(PatternListTest.Bean.class, PatternListTest$BeanValidationAdapter::new)
          .build();

  @Test
  public void testNoViolations() {
    final Bean bean = new Bean();
    bean.field = "ABCD";

    validator.validate(bean);
    // no exception
  }

  @Test
  public void testMultipleViolations() {
    final Bean bean = new Bean();
    bean.field = "f";

    final ConstraintViolationException exceptions =
        assertThrows(
            ConstraintViolationException.class,
            () -> {
              validator.validate(bean);
            });

    for (final ConstraintViolation violation : exceptions.violations()) {
      System.out.println("Violation: " + violation.toString());
    }

    assertEquals(5, exceptions.violations().size());

    /*
    Test Output:

    Violation: ConstraintViolation[path=field, field=field, message=Missing ABCD]
    Violation: ConstraintViolation[path=field, field=field, message=Missing B]
    Violation: ConstraintViolation[path=field, field=field, message=Missing D]

    org.opentest4j.AssertionFailedError: expected: <5> but was: <3>
    */
  }
}
