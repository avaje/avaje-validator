package example.avaje.mapcascade;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Valid
public class MapCascade {

  private final Map<String, MapTemplate> dynamicBlocks;

  public MapCascade(Map<String, MapTemplate> dynamicBlocks) {
    this.dynamicBlocks = dynamicBlocks;
  }

  @Valid
  @NotNull
  @Size(min = 1)
  public Map<String, MapTemplate> getDynamicBlocks() {
    return dynamicBlocks;
  }
}
