package example.avaje.composable;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@Valid
public record MySerialExample(
  @MySerial String key,
  @NotBlank String value) {
}
