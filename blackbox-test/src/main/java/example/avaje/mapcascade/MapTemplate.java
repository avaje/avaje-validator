package example.avaje.mapcascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@Valid
public class MapTemplate {

  private final String name;

  public MapTemplate(String name) {
    this.name = name;
  }

  @NotBlank
  public String getName() {
    return name;
  }
}
