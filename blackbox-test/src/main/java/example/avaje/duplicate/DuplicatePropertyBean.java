package example.avaje.duplicate;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@io.avaje.validation.constraints.Valid
public class DuplicatePropertyBean {

  @NotNull
  @Size(min = 1, max = 64)
  private String gridId;

  private List<@Valid DuplicatePropertyChild> gridSections = List.of();

  private Map<String, @Valid DuplicatePropertyChild> items = Map.of();

  @NotNull
  @Size(min = 1, max = 64)
  public String getGridId() {
    return gridId;
  }

  public void setGridId(String gridId) {
    this.gridId = gridId;
  }

  @NotNull
  @Valid
  public List<@Valid DuplicatePropertyChild> getGridSections() {
    return gridSections;
  }

  public void setGridSections(List<@Valid DuplicatePropertyChild> gridSections) {
    this.gridSections = gridSections;
  }

  @NotNull
  @Valid
  public Map<String, @Valid DuplicatePropertyChild> getItems() {
    return items;
  }

  public void setItems(Map<String, @Valid DuplicatePropertyChild> items) {
    this.items = items;
  }
}
