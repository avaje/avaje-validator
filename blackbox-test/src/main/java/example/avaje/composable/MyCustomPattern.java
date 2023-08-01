package example.avaje.composable;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@Valid
public record MyCustomPattern(
  @MyKey String key,
  @NotBlank String value) {
}
