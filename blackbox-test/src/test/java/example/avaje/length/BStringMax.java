package example.avaje.length;

import io.avaje.validation.constraints.Max;
import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@Valid
public record BStringMax (
  @NotBlank @Max(4) String strVal,
  @Max(4) int intVal
){
}
