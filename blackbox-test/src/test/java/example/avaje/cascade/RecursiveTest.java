package example.avaje.cascade;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class RecursiveTest {

  Validator validator = Validator.builder().build();

  @Test
  void valid() {
    var recurse =
        new Recursive("recursive1", new Recursive("recursive2", new Recursive("recursive3", null)));
    assertThat(validator.check(recurse).iterator().next())
        .matches(c -> "child.child.child".equals(c.path()));
  }
}
