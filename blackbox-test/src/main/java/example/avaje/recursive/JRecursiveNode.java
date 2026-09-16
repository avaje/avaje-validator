package example.avaje.recursive;

import java.util.ArrayList;
import java.util.List;

import io.avaje.validation.constraints.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Valid
public class JRecursiveNode {

  private String id;

  private List<@Valid JRecursiveNode> children = new ArrayList<>();

  public JRecursiveNode() {}

  public JRecursiveNode(String id) {
    this.id = id;
  }

  @NotNull
  @Size(min = 1, max = 64)
  public String getId() {
    return id;
  }

  @NotNull
  @Valid
  public List<@Valid JRecursiveNode> getChildren() {
    return children;
  }

  public void setChildren(List<@Valid JRecursiveNode> children) {
    this.children = children;
  }
}
