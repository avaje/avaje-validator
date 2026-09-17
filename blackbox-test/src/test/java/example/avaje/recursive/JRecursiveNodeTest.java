package example.avaje.recursive;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class JRecursiveNodeTest {

  final Validator validator = Validator.builder().build();

  @Test
  void valid() {
    final var node = new JRecursiveNode("root");
    node.setChildren(List.of(new JRecursiveNode("child")));

    assertThat(validator.check(node)).isEmpty();
  }

  @Test
  void invalidChild_isValidated() {
    final var node = new JRecursiveNode("root");
    node.setChildren(List.of(new JRecursiveNode()));

    assertThat(validator.check(node))
        .extracting("path")
        .containsExactly("children[0].getId", "getChildren[0].getId");
  }

  @Test
  void multipleChildren_validateEachElement() {
    final var node = new JRecursiveNode("root");
    node.setChildren(
        List.of(new JRecursiveNode("valid"), new JRecursiveNode(), new JRecursiveNode("valid")));

    assertThat(validator.check(node))
        .extracting("path")
        .containsExactly("children[1].getId", "getChildren[1].getId");
  }

  @Test
  void invalidGrandchild_isValidated() {
    final var grandchild = new JRecursiveNode();
    final var child = new JRecursiveNode("child");
    child.setChildren(List.of(grandchild));
    final var node = new JRecursiveNode("root");
    node.setChildren(List.of(child));

    assertThat(validator.check(node))
        .extracting("path")
        .containsExactly(
            "children[0].children[0].getId",
            "children[0].getChildren[0].getId",
            "getChildren[0].children[0].getId",
            "getChildren[0].getChildren[0].getId");
  }

  @Test
  void emptyChildren_isValid() {
    final var node = new JRecursiveNode("root");
    node.setChildren(List.of());

    assertThat(validator.check(node)).isEmpty();
  }

  @Test
  void nullChildren_isInvalid() {
    final var node = new JRecursiveNode("root");
    node.setChildren(null);

    assertThat(validator.check(node))
        .extracting("path")
        .containsExactly("getChildren");
  }

  @Test
  void nullChildElement_isValid() {
    final var node = new JRecursiveNode("root");
    node.setChildren(Arrays.asList((JRecursiveNode) null));

    assertThat(validator.check(node)).isEmpty();
  }
}
