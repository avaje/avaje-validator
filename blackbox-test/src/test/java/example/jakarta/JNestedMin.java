package example.jakarta;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Valid
public class JNestedMin {

  public List<@Min(1L) Long> fieldValues = List.of(1L);
}
