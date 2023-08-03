package example.avaje.length;

import io.avaje.validation.constraints.Max;
import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@Valid
public record BStringMax2(
  @NotBlank @Max(value = 4, message = "customMaxStr4") String strVal,
  @Max(value = 4, message = "customMaxInt4") int intVal
){
}
