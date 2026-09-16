package example.avaje.duplicate;

import io.avaje.validation.constraints.Valid;
import jakarta.validation.constraints.NotNull;

@Valid
public class DuplicatePropertyChild {

  @NotNull
  private String id;

  public DuplicatePropertyChild() {}

  public DuplicatePropertyChild(String id) {
    this.id = id;
  }

  @NotNull
  public String getId() {
    return id;
  }
}
