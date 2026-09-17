package example.jakarta;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Valid
public class JNestedMinGetter {

  private List<Long> values = List.of(1L);

  public List<@Min(1L) Long> getValues() {
    return values;
  }

  public void setValues(List<Long> values) {
    this.values = values;
  }
}
